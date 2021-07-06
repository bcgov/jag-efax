package ca.bc.gov.ag.efax.ws.scheduling;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessUpdate;
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
    
    @Scheduled(fixedDelayString = "${ws.faxTimeoutCheck}")
    public void sentFaxTimeout() {
        // Every ${ws.faxTimeoutCheck} milliseconds, check if there are any queued SentMessages in redis whose createdTimestamp is older than ${ws.faxTimeout} milliseconds.
        // If so, then remove the record from redis and send a callback to the client indicating the message timed out.

        logger.trace("Checking for timed out fax messages");
        
        long now = new Date().getTime();
        
        for (SentMessage sentMessage : sentMessageRepository.findAll()) {
            Date createdTs = sentMessage.getCreatedTs();
            if (createdTs.getTime() + faxTimeout < now) {
                logger.debug("Sent message uuid:{} timed out", sentMessage.getUuid());

                // send a callback to the client
                documentDistributionService.sendResponseToCallback(getResponse(sentMessage.getJobId()));
                
                // TODO: if the callback fails to send, should we add a retry counter to the redis object and try again later, perhaps failing after 3 retries?
                
                // remove message from queue
                sentMessageRepository.deleteById(sentMessage.getUuid());
                
            }
        }
    }
    
    /**
     * Returns the error response object to send to Justin's SOAP callback service.
     */
    private DocumentDistributionMainProcessProcessUpdate getResponse(String jobId) {
        DocumentDistributionMainProcessProcessUpdate response = new DocumentDistributionMainProcessProcessUpdate();
        response.setJobId(jobId);
        FAXTimeoutFault fault = new FAXTimeoutFault();
        response.setStatus(fault.getFaultCode());
        response.setStatusMsg(fault.getFaultMessage());
        return response;
    }
    
}
