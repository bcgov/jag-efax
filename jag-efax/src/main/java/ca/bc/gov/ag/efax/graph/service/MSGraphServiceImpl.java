package ca.bc.gov.ag.efax.graph.service;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
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
import ca.bc.gov.ag.efax.graph.utils.EFaxGraphConstants;
import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.mail.util.FileUtils;
import ca.bc.gov.ag.efax.pdf.service.PdfService;

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

	private MSGraphProperties gProps;
	
	private GraphServiceClientComp gComp;
	
    private PdfService pdfService;

	public MSGraphServiceImpl(MSGraphProperties gProps, GraphServiceClientComp gComp, PdfService pdfService) {
		this.gProps = gProps;
		this.gComp = gComp;
		this.pdfService = pdfService; 
	}

	@jakarta.annotation.PostConstruct
	private void postConstruct() {
		logger.info("MS Graph Service Impl started.");
	}

	/**
	 * Read all messages from MS Graph service account inbox.  
	 * 
	 */
	@Override
	public MessageCollectionResponse GetMessages() throws Exception {
		
		// Capture by Date Received, Ascending order in TEXT format (e.g., strip HTML).    
		return gComp.getGraphClient().users().byUserId(gProps.getEmailAccount()).mailFolders().byMailFolderId("Inbox")
				.messages().get(requestConfiguration -> {
					requestConfiguration.queryParameters.orderby = new String []{"receivedDateTime asc"};
					requestConfiguration.headers.add("Prefer", "outlook.body-content-type=\"text\"");
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
	public void sendMessage(MailMessage mailMessage) throws Exception {

		com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody sendMailPostRequestBody = new com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody();

		Message message = new Message();
		message.setSubject(mailMessage.getSubject());
		ItemBody ib = new ItemBody();
		// TODO - see here re body as text or html. 
		//ib.setContentType(BodyType.Text);
		ib.setContentType(BodyType.Html);
		ib.setContent(mailMessage.getBody());
		message.setBody(ib);

		// Populate recipient(s)
		message.setToRecipients(new ArrayList<Recipient>()); 
		String[] emailArray = mailMessage.getTo().split(",");
		for (String email : emailArray) {
			Recipient toRecipient = new Recipient();
			EmailAddress emailAddress = new EmailAddress();
			emailAddress.setAddress(email);
			toRecipient.setEmailAddress(emailAddress);
			message.getToRecipients().add(toRecipient);
	    }

		// Add attachment(s)
		if (null != mailMessage.getAttachments() && mailMessage.getAttachments().size() > 0) {

			LinkedList<Attachment> attachments = new LinkedList<Attachment>();

			// Fetch and load the file for each attachment url. 
			List<String> attachmentURLs = mailMessage.getAttachments(); 
			if (attachmentURLs != null && attachmentURLs.size() > 0 ) {
				for (int i = 0; i < attachmentURLs.size(); i++) {
					String url = attachmentURLs.get(i);
					String fileName = FileUtils.getTempFilename(mailMessage, i);
					File file = readFileFromURL(new URL(url), fileName, mailMessage.getJobId());
					FileAttachment attachment = new FileAttachment();
					attachment.setOdataType("#microsoft.graph.fileAttachment");
					attachment.setName(fileName);
					attachment.setContentType("application/pdf");
					attachment.setContentBytes(FileUtils.fileToByteArray(file));
					attachments.add(attachment);
				}
			}
			
			message.setAttachments(attachments);	
			
		}

		sendMailPostRequestBody.setSaveToSentItems(gProps.isSaveToSent());
		sendMailPostRequestBody.setMessage(message);

		// Send mail from the MS Graph API service account to FAX service.
		gComp.getGraphClient().users().byUserId(gProps.getEmailAccount()).sendMail().post(sendMailPostRequestBody);

	}
	
	/**
	 * Moves a message to the 'deleted' items folder. 
	 *
	 * Assumption: all inbox items have a unique id generated by MS.
	 *
	 * @throws Exception 
	 * 
	 */
	@Override
	public void deleteMessage(String id) throws Exception {
		moveItemById(id, "deleteditems");
	}

	/**
	 * Gets Secret Key expiry date.  
	 * @return  
	 *
	 * Reference: https://learn.microsoft.com/en-us/graph/api/application-get?view=graph-rest-1.0&tabs=java
	 * 
	 */
	@Override
	public String getPasswordCredentialsExpiryDate() {
			
		Application a = gComp.getGraphClient().applicationsWithAppId(gProps.getClientId()).get(requestConfiguration -> {
			requestConfiguration.queryParameters.select = new String []{"passwordCredentials"};
		});
				
		OffsetDateTime dt = a.getPasswordCredentials().get(0).getEndDateTime();
		logger.debug("MS Graph API Secret Key expiration date: " + dt.toLocalDate());
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(EFaxGraphConstants.dateFormat);
		return fmt.format(dt);
		
	}
	
	/**
	 * 
	 * Moves an item to another mailbox. 
	 * 
	 * @param inboxItemById
	 * @param destinationId
	 */
	private void moveItemById(String inboxItemId, String destinationId) {

		com.microsoft.graph.users.item.messages.item.move.MovePostRequestBody movePostRequestBody = new com.microsoft.graph.users.item.messages.item.move.MovePostRequestBody();
		movePostRequestBody.setDestinationId(destinationId);

		// This requires permissions: Mail.ReadWrite
		gComp.getGraphClient().users().byUserId(gProps.getEmailAccount()).messages().byMessageId(inboxItemId).move()
				.post(movePostRequestBody);

	}
	
	
	/**
	 * 
	 * Read remote file into File. 
	 * 
	 * @param url
	 * @param fileName
	 * @param jobId
	 * @return
	 * @throws Exception
	 */
	private File readFileFromURL(final URL url, final String fileName, String jobId) throws Exception {
		InputStream inputStream = null;

		try {

			String path = gProps.getTempDirectory() + fileName;

			// try first to flatten the PDF
			File file = pdfService.flattenPdf(url, path, jobId);

			// if unsuccessful, simply download the file as is
			if (file == null) {
				logger.info("PDF Flattening: jobId {} not flattened, using original file.", jobId);
				// Open the URL and get metadata
				inputStream = url.openStream();
				Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
				file = new File(path);
			}

			return file;
		} finally {
			if (inputStream != null)
				IOUtils.closeQuietly(inputStream);
		}
	}

	@Override
	public String getApplicationName() {
		
		return gProps.getAzureAppName();
		
		// TODO - This could be improved upon to fetch this name from MS Graph API. Something like:  
		//Application a = gComp.getGraphClient().applications().byApplicationId("{application-id}").get();
		//return a.getAppId();
	}
}
