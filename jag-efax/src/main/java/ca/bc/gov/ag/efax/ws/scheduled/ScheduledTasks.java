package ca.bc.gov.ag.efax.ws.scheduled;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import ca.bc.gov.ag.efax.ws.exception.FAXTimeoutFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;
import ca.bc.gov.ag.efax.ws.service.DocumentDistributionService;

@Component
@ConditionalOnProperty(
        name = "ws.timeout.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ScheduledTasks {

    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Value(value = "${ws.timeout.duration}")
    private long faxTimeout;

    @Autowired
    private SentMessageRepository sentMessageRepository;

    @Autowired
    private DocumentDistributionService documentDistributionService;

    @Scheduled(fixedDelayString = "${ws.timeout.pollInterval}")
    public void sentFaxTimeout() {
        // Every ${ws.timeout.pollInterval} milliseconds, check if there are any queued SentMessages in redis whose createdTimestamp is older than
        // ${ws.timeout.duration} milliseconds.
        // If so, then remove the record from redis and send a callback to the client indicating the message timed out.
        logger.trace("Checking for timed out fax messages");

        for (SentMessage sentMessage : sentMessageRepository.findAll()) {
            if (!hasTimedOut(sentMessage))
                continue;

            logger.info("Sent message uuid:{} timed out", sentMessage.getUuid());

            // send a callback to the client
            try {
                documentDistributionService.sendResponseToCallback(getResponse(sentMessage.getJobId()));
            } catch (Exception e) {
                logger.error("Could not invoke a Justin callback for jobId: {}", sentMessage.getJobId());
                logger.error(e.getMessage(), e);
            }

            // remove message from queue
            logger.debug("Removing message from redis queue: " + sentMessage.getUuid());
            sentMessageRepository.deleteById(sentMessage.getUuid());
        }
    }

    /**
     * Returns true of the queued redis message has timed out (ie, created > 25 minutes ago)
     */
    private boolean hasTimedOut(SentMessage sentMessage) {
        // NPE fix.  For some reason under heavy load redis can return null records.
        if (sentMessage == null) {            
            return false;
        }
        long now = new Date().getTime();
        Date createdTs = sentMessage.getCreatedTs();
        return createdTs.getTime() + faxTimeout < now;
    }

    /**
     * Returns the error response object to send to Justin's SOAP callback service.
     */
    private DocumentDistributionMainProcessProcessResponse getResponse(String jobId) {
        DocumentDistributionMainProcessProcessResponse response = new DocumentDistributionMainProcessProcessResponse();
        response.setJobId(jobId);
        FAXTimeoutFault fault = new FAXTimeoutFault();
        response.setStatusCode(fault.getFaultCode());
        response.setStatusMessage(fault.getFaultMessage());
        return response;
    }

}
