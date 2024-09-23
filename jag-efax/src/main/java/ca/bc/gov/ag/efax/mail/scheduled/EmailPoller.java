package ca.bc.gov.ag.efax.mail.scheduled;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.efax.graph.model.GmailMessage;
import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import ca.bc.gov.ag.efax.mail.service.EmailService;
import ca.bc.gov.ag.efax.mail.service.parser.EmailParser;
import ca.bc.gov.ag.efax.ws.service.DocumentDistributionService;

@Component
@ConditionalOnProperty(
        name = "exchange.poller.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class EmailPoller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailParser emailParser;

    @Autowired
    private DocumentDistributionService documentDistributionService;

    @Autowired
    private SentMessageRepository sentMessageRepository;

    @Scheduled(fixedDelayString = "${exchange.poller.interval}")
    public void pollForEmails() throws Exception {
        logger.debug("Started email inbox poll.");
        
        // Retrieve all INBOX emails
        for (GmailMessage emailMessage : emailService.getInboxEmails()) {	
            
            // Attempt to parse the email response from MS Graph API. 
            DocumentDistributionMainProcessProcessResponseDecorator response = emailParser.parse(emailMessage);

            // If the jobId is blank, there's nothing we can do except move on. (We can't send a message to the Justin Callback informing the user
            // that a message with a certain jobId succeeded or failed).  If so, an error message should have been logged for review so the email
            // parser can be improved upon.
            if (!StringUtils.isEmpty(response.getJobId())) {                     
                documentDistributionService.sendResponseToCallback(response);
                
                // remove the referenced message from the redis queue
                removeFromQueue(response.getUuid());
            }
            
            // Move email to the "Deleted Items" folder so we don't process this same email again.
            emailService.deleteEmail(emailMessage.getId());
        }

        logger.debug("Finished email inbox poll.");
    }

    /**
     * Removes from the redis queue the record with the named id
     * @param uuid
     */
    private void removeFromQueue(String uuid) {
        try {
            logger.debug("Removing message from redis queue: " + uuid);
            sentMessageRepository.deleteById(uuid);
        } catch (Exception e) {
            logger.error("Could not remove message with uuid '{}' from queue", uuid);
            // Do nothing. The queued item will get removed when the faxTimeout scheduled task runs.
        }
    }
}
