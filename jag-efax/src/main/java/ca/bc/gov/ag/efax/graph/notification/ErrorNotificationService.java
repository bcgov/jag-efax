package ca.bc.gov.ag.efax.graph.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.bc.gov.ag.efax.graph.config.MSGraphProperties;
import ca.bc.gov.ag.efax.graph.service.MSGraphService;
import ca.bc.gov.ag.efax.graph.service.MSGraphServiceImpl;
import ca.bc.gov.ag.efax.graph.utils.EFaxGraphConstants;
import ca.bc.gov.ag.efax.mail.model.MailMessage;

@Service
public class ErrorNotificationService {
	
	private static final Logger logger = LoggerFactory.getLogger(ErrorNotificationService.class);
	
	private MSGraphProperties gProps; 
	
	private MSGraphService gService;
	
	public ErrorNotificationService(MSGraphProperties gProps, MSGraphServiceImpl gService) {
		this.gProps = gProps; 
		this.gService = gService; 
	}

	/**
	 * 
	 * Send an early warning email before MS Graph Credentials need to be refreshed.  
	 * 
	 * @param daysRemaining
	 * @param appName
	 */
	public void sendMSGraphCredentialWarning(long daysRemaining, String appName) {
		
		MailMessage mailMessage = new MailMessage();
		mailMessage.setSubject(EFaxGraphConstants.ExpiryWarningSubject);
		mailMessage.setBody(String.format(EFaxGraphConstants.ExpiryWarningMsg, 
					daysRemaining, appName));
		mailMessage.setTo(gProps.getAdminEmail());
		
		try {
			gService.sendMessage(mailMessage);
		} catch (Exception e) {
			logger.error("Unable to send MS Graph API warning message. Reason: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
