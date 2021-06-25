package ca.bc.gov.ag.mail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
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
import ca.bc.gov.ag.model.MailMessage;
import ca.bc.gov.ag.model.SentMessage;
import ca.bc.gov.ag.repository.SentMessageRepository;

/**
 * This {@link MailController} exposes the {@link MailService} in a REST API.
 */
@RequestMapping("/v1")
@RestController
public class MailController {

    private Logger logger = LoggerFactory.getLogger(MailController.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private SentMessageRepository sentMessageRepository;

    /**
     * This method accepts a {@link MailMessage} as a POST request to create and send an email using Microsoft Exchange server.
     * 
     * @param mailMessage contains a uuid, to, subject, and body components, as well as a list of URIs (Strings) to include as attachments.
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

            // Store the UUID of this message in the redis queue. To avoid race conditions, this is stored now (before the actual sending of the
            // message) and removed if there is an error.
            SentMessage sentMessage = new SentMessage(mailMessage.getUuid(), mailMessage.getJobId(), new Date());
            sentMessageRepository.save(sentMessage);

            mailService.sendMessage(mailMessage);

        } catch (Exception e) {
            logger.error("Error sending message, uuid: {}", mailMessage.getUuid());
            logger.error("Exception:", e);
            sentMessageRepository.deleteById(mailMessage.getUuid());
            throw e;
        }
        logger.debug("Message pushed to ExchangeServer, uuid: {}", mailMessage.getUuid());
    }

}
