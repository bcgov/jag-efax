package ca.bc.gov.ag.efax.ws.scheduling;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;

@Component
public class ScheduledTasks {

    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    
    @Value(value = "${ws.faxTimeout}")
    private long faxTimeout; 
    
    @Autowired
    private SentMessageRepository sentMessageRepository;
    
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
                
                // remove message from queue
                sentMessageRepository.deleteById(sentMessage.getUuid());
                
                // send a callback to the client
                // TODO: implement callback letting client know of a failed message.
            }
        }
        
    }
    
}
