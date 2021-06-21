package ca.bc.gov.ag.mail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.exception.MailException;

/**
 * This {@link MailController} exposes the {@link MailService} in a REST API.
 */
@RequestMapping("/v1")
@RestController
public class MailController { 

	private Logger logger = LoggerFactory.getLogger(MailController.class);
	
	@Autowired
	private MailService mailService;
	
	/**
	 * This method accepts a {@link MailMessage} as a POST request to create and send an email using Microsoft Exchange server.
	 * 
	 * @param mailMessage contains a uuid, to, subject, and body components, as well as a list of URIs (Strings) to include
	 *                    as attachments.
	 * @throws URISyntaxException, MailException 
	 * @throws MailException if any error occurs during delivery.
	 */
	@PostMapping(path = "/sendMessage", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void sendMessage(@RequestBody @Valid MailMessage mailMessage) throws URISyntaxException, MailException {
		logger.debug("Request to /sendMessage, uuid: {}", mailMessage.getUuid());
		
		try {
			List<String> attachments = ListUtils.emptyIfNull(mailMessage.getAttachments());
			URI[] attachmentRefs = new URI[attachments.size()];
			for (int i = 0; i < attachmentRefs.length; i++) {
				attachmentRefs[i] = new URI(attachments.get(i));
			}
			
			mailService.sendMessage(mailMessage);
		} catch (URISyntaxException | MailException e) {
			logger.error("Error sending message, uuid: {}", mailMessage.getUuid());
			logger.error("Exception:", e);
			throw e;
		} 
		logger.debug("Message pushed to ExchangeServer, uuid: {}", mailMessage.getUuid());
	}
	
}
