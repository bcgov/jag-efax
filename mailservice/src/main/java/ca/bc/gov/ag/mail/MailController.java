package ca.bc.gov.ag.mail;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.exception.MailException;

@RestController
@RequestMapping("/v1")
public class MailController {

	private Logger logger = LoggerFactory.getLogger(MailController.class);
	
	@Autowired
	private MailService mailService;
	
	@PostMapping(path = "/sendMessage", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void sendMessage(@RequestBody MailMessage mailMessage) throws MailException {
		logger.debug("Request to /api/v1/sendMessage, uuid: {}", mailMessage.getUuid());
		
		try {
			List<String> attachments = mailMessage.getAttachments();
			URI[] attachmentRefs = new URI[attachments.size()];
			for (int i = 0; i < attachmentRefs.length; i++) {
				attachmentRefs[i] = new URI(attachments.get(i));
			}
			
			mailService.sendMessage(
					mailMessage.getUuid(), 
					mailMessage.getTo(), 
					mailMessage.getSubject(), 
					mailMessage.getBody(), 
					attachmentRefs);
		} catch (Exception e) {
			logger.error("Error sending message, uuid: {}", mailMessage.getUuid());
			logger.error("Exception:", e);
			throw new MailException("Could not send message", e);
		}
		finally {
			logger.debug("Message pushed to ExchangeServer, uuid: {}", mailMessage.getUuid());
		}
	}
	
}
