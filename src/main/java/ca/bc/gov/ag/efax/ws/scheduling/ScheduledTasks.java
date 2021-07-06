package ca.bc.gov.ag.efax.ws.scheduling;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessResponse;
import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import ca.bc.gov.ag.efax.ws.exception.FAXTimeoutFault;
import ca.bc.gov.ag.efax.ws.service.DocumentDistributionService;

@Component
public class ScheduledTasks {

    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    
    @Value(value = "${ws.faxTimeout}")
    private long faxTimeout; 
    
    @Autowired
    private SentMessageRepository sentMessageRepository;
    
    @Autowired
    private DocumentDistributionService documentDistributionService;
    
    @Scheduled(fixedDelayString = "${ws.faxTimeoutPoll}")
    public void sentFaxTimeout() {
        // Every ${ws.faxTimeoutPoll} milliseconds, check if there are any queued SentMessages in redis whose createdTimestamp is older than ${ws.faxTimeout} milliseconds.
        // If so, then remove the record from redis and send a callback to the client indicating the message timed out.

        logger.trace("Checking for timed out fax messages");
        
        long now = new Date().getTime();
        
        for (SentMessage sentMessage : sentMessageRepository.findAll()) {
            Date createdTs = sentMessage.getCreatedTs();
            if (createdTs.getTime() + faxTimeout < now) {
                logger.debug("Sent message uuid:{} timed out", sentMessage.getUuid());

                // send a callback to the client
                try {                    
                    documentDistributionService.sendResponseToCallback(getResponse(sentMessage.getJobId()));
                }
                catch (Exception e) {
                    logger.error("Could not invoke a Justin callback for jobId: {}", sentMessage.getJobId());
                    logger.error(e.getMessage(), e);
                }
                
                // remove message from queue
                sentMessageRepository.deleteById(sentMessage.getUuid());                
            }
        }
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
