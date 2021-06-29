package ca.bc.gov.ag.fl.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.fl.service.EmailService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

@Component
public class EmailPoller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private EmailService emailService;
    
    @Value(value = "${exchange.poller.enabled}")
    private boolean enabled;
    
    @Scheduled(fixedDelayString = "${exchange.poller.interval}")
    public void pollForEmails() throws Exception {
        if (!enabled) {
            return;
        }
        
        logger.debug("Started email indox poll.");
        
        List<EmailMessage> emailMessages = emailService.getEfaxInboxEmails();
        
        logger.debug("Retrieved {} emails", emailMessages.size());
        
        // TODO: EFAX-24 parse emails, looking for jobId and status of the sent fax
    }
    
}
