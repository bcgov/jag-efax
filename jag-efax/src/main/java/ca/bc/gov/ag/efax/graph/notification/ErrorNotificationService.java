package ca.bc.gov.ag.efax.graph.notification;

import java.util.ArrayList;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;

import ca.bc.gov.ag.efax.graph.config.MSGraphProperties;
import ca.bc.gov.ag.efax.graph.service.MSGraphService;
import ca.bc.gov.ag.efax.graph.service.MSGraphServiceImpl;
import ca.bc.gov.ag.efax.graph.utils.EFaxGraphConstants;
import ca.bc.gov.ag.efax.mail.model.MailMessage;

public class ErrorNotificationService {
	
	private static final Logger logger = LoggerFactory.getLogger(ErrorNotificationService.class);
	
	private MSGraphProperties gProps; 
	
	private MSGraphService gService;
	
	public ErrorNotificationService(MSGraphProperties gProps, MSGraphServiceImpl gService) {
		this.gProps = gProps; 
		this.gService = gService; 
	}

	public void sendMSGraphCredentialWarning(long daysRemaining, String appName) {
		
		MailMessage mailMessage = new MailMessage();
		mailMessage.setBody(String.format(EFaxGraphConstants.ExpiryWaring, daysRemaining, appName));
		mailMessage.setTo(gProps.getAdminEmail());
		
//		Message message = new Message();
//		message.setToRecipients(new ArrayList<Recipient>());
//		LinkedList<Recipient> toRecipientsList = new LinkedList<Recipient>();
//		
//		String[] adminArray = gProps.getAdminEmail().split(",");
//		for (String adminEmail : adminArray) {
//			Recipient toRecipient = new Recipient();
//			EmailAddress emailAddress = new EmailAddress();
//			emailAddress.setAddress(adminEmail);
//			toRecipient.setEmailAddress(emailAddress);
//			message.getToRecipients().add(toRecipient);
//	    }
		
		try {
			gService.sendMessage(mailMessage);
		} catch (Exception e) {
			logger.error("Unable to send MS Graph API warning message. Reason: " + e.getMessage());
			e.printStackTrace();
		}
		
	}

}
