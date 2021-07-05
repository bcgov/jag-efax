package ca.bc.gov.ag.efax.mail.scheduler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessUpdate;
import ca.bc.gov.ag.efax.mail.service.EmailService;
import ca.bc.gov.ag.efax.mail.service.parser.EmailParser;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

@Component
public class EmailPoller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailParser emailParser;

    @Value(value = "${exchange.poller.enabled}")
    private boolean enabled;

    @Scheduled(fixedDelayString = "${exchange.poller.interval}")
    public void pollForEmails() throws Exception {
        if (!enabled) {
            return;
        }

        logger.debug("Started email inbox poll.");

        // Retrieve all INBOX emails (that was originally sent to IMCEAFAX) 
        for (EmailMessage emailMessage : emailService.getEfaxInboxEmails()) {
            
            // Attempt to parse the email response from MS Exchange
            DocumentDistributionMainProcessProcessUpdate response = emailParser.parse(emailMessage);

            // If the jobId is blank, there's nothing we can do except move on. (We can't send a message to the Justin Callback informing the user
            // that a message with a certain jobId succeeded or failed).  If so, an error message should have been logged for review so the email
            // parser can be improved upon.
            if (!StringUtils.isEmpty(response.getJobId())) {                     
                // TODO: send message to Justin Callback informing user of the jobId and status.
            }
        }

        logger.debug("Finished email inbox poll.");
    }

}
