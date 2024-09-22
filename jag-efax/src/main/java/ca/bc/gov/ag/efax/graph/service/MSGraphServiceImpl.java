package ca.bc.gov.ag.efax.graph.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.microsoft.graph.models.Application;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.MessageCollectionResponse;
import com.microsoft.graph.models.Recipient;

import ca.bc.gov.ag.efax.graph.comp.GraphServiceClientComp;
import ca.bc.gov.ag.efax.graph.config.MSGraphProperties;
import ca.bc.gov.ag.efax.graph.model.EmailMessage;
import ca.bc.gov.ag.efax.graph.model.MailMessage;
import ca.bc.gov.ag.efax.graph.utils.EFaxGraphConstants;

/**
 * 
 * MS Graph Email Service methods
 * 
 * @author 176899
 * 
 * Permissions required (to-date)
 *  
 *  Mail.Read
 *  Mail.Send 
 * 	Mail.ReadWrite
 *  User.Read
 * 
 */
@Service
public class MSGraphServiceImpl implements MSGraphService {

	private static final Logger logger = LoggerFactory.getLogger(MSGraphServiceImpl.class);

	private MSGraphProperties props;
	private GraphServiceClientComp gComp;

	public MSGraphServiceImpl(MSGraphProperties props, GraphServiceClientComp gComp) {
		this.props = props;
		this.gComp = gComp;
	}

	@javax.annotation.PostConstruct
	private void postConstruct() {
		logger.info("MS Graph Service Impl started.");
	}

	/**
	 * Read all unread messages having the subject "Message Succeeded" and not
	 * previously read.
	 */
	@Override
	public MessageCollectionResponse GetMessages() throws Exception {
		
		// Capture by Date Received, Ascending order.  
		return gComp.getGraphClient().users().byUserId(props.getMsgEmailAccount()).mailFolders().byMailFolderId("Inbox")
				.messages().get(requestConfiguration -> {
					requestConfiguration.queryParameters.orderby = new String []{"receivedDateTime asc"};
				});
		
	}

	/**
	 * 
	 * SendMessage 
	 * 
	 * Reference: // https://learn.microsoft.com/en-us/graph/api/user-sendmail?view=graph-rest-1.0&tabs=java
	 * 
	 */
	@Override
	public void sendMessage(MailMessage mailMessage, boolean saveToSentItems) throws Exception {

		com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody sendMailPostRequestBody = new com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody();

		// TODO - what to do with guid and other mailMessage attributes?
		// This to be determined when this service is integrated with the eFax service.
		Message message = new Message();
		message.setSubject(mailMessage.getSubject());
		ItemBody ib = new ItemBody();
		ib.setContentType(BodyType.Text);
		ib.setContent(mailMessage.getBody());
		message.setBody(ib);

		// Populate recipients
		LinkedList<Recipient> toRecipientsList = new LinkedList<Recipient>();
		Recipient toRecipients = new Recipient();
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.setAddress(mailMessage.getTo());
		toRecipients.setEmailAddress(emailAddress);
		toRecipientsList.add(toRecipients);
		message.setToRecipients(new ArrayList<Recipient>());
		message.getToRecipients().add(toRecipients);

		// Add attachment(s)
		if (null != mailMessage.getAttachments() && mailMessage.getAttachments().size() > 0) {

			LinkedList<Attachment> attachments = new LinkedList<Attachment>();

			// TODO - Set to text for now but eventually attachment needs to be be PDF.
			for (String attach : mailMessage.getAttachments()) {
				FileAttachment attachment = new FileAttachment();
				attachment.setOdataType("#microsoft.graph.fileAttachment");
				attachment.setName("attachment.txt");
				attachment.setContentType("text/plain");
				attachment.setContentBytes(Base64.getDecoder().decode(attach));
				attachments.add(attachment);
			}
			message.setAttachments(attachments);
		}

		sendMailPostRequestBody.setSaveToSentItems(saveToSentItems);
		sendMailPostRequestBody.setMessage(message);

		// Send mail from the main account to the emailAddress.to address.
		gComp.getGraphClient().users().byUserId(props.getMsgEmailAccount()).sendMail().post(sendMailPostRequestBody);

	}
	
	/**
	 * Moves a message with given subject to 'deleted' items folder. 
	 * 
	 * Assumption: all inbox items have a unique subject for eFax.
	 * @throws Exception 
	 * 
	 */
	@Override
	public void deleteMessage(EmailMessage emailMessage) throws Exception {
		
		String id = getInboxItemById(emailMessage.getSubject());
		
		if ( null != id ) {	
			moveItemById(id, "deleteditems");
		}

	}

	/**
	 * Useful for determining Secret Key expiry date.  
	 * @return  
	 *
	 * Reference: https://learn.microsoft.com/en-us/graph/api/application-get?view=graph-rest-1.0&tabs=java
	 * 
	 */
	@Override
	public String getPasswordCredentialsExpiryDate() {
			
		Application a = gComp.getGraphClient().applicationsWithAppId(props.getMsgClientId()).get(requestConfiguration -> {
			requestConfiguration.queryParameters.select = new String []{"passwordCredentials"};
		});
				
		OffsetDateTime dt = a.getPasswordCredentials().get(0).getEndDateTime();
		logger.debug("MS Graph API Secret Key expiration date: " + dt.toLocalDate());
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(EFaxGraphConstants.dateFormat);
		return fmt.format(dt);
		
	}
	/**
	 * 
	 * ex: https://graph.microsoft.com/v1.0/users('JAG11_T@extest.gov.bc.ca')/mailFolders/Inbox/messages?$filter=subject%20eq%20'Message%20Succeeded:%207785720693'
	 * 
	 * @param subject
	 * @return
	 * @throws Exception 
	 */
	private String getInboxItemById(String subject) throws Exception {
		
		MessageCollectionResponse messages =  gComp.getGraphClient().users().byUserId(props.getMsgEmailAccount()).mailFolders().byMailFolderId("Inbox")
				.messages().get(requestConfiguration -> {
					requestConfiguration.queryParameters.filter = "subject eq '" + subject + "'";
				});
		
		if (null != messages.getValue() && messages.getValue().size() > 0) {
			logger.debug("Number of messages found having the given subject were: " + messages.getValue().size());
			if (messages.getValue().size() > 1) {
				throw new Exception("An unexpected number of messages (" + messages.getValue().size() + ") were found having the given subject of: " + subject + ".");
			} else if (messages.getValue().size() == 1) {
				return messages.getValue().get(0).getId();
			};
		}
		
		return null; 
	}
	
	/**
	 * 
	 * Moves an item to another mailbox. 
	 * 
	 * @param inboxItemById
	 * @param destinationId
	 */
	private void moveItemById(String inboxItemById, String destinationId) {

		com.microsoft.graph.users.item.messages.item.move.MovePostRequestBody movePostRequestBody = new com.microsoft.graph.users.item.messages.item.move.MovePostRequestBody();
		movePostRequestBody.setDestinationId(destinationId);

		// This requires permissions: Mail.ReadWrite
		gComp.getGraphClient().users().byUserId(props.getMsgEmailAccount()).messages().byMessageId(inboxItemById).move()
				.post(movePostRequestBody);

	}

}
