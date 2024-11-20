package ca.bc.gov.ag.efax.mail.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.MessageCollectionResponse;

import ca.bc.gov.ag.efax.graph.model.GmailMessage;
import ca.bc.gov.ag.efax.graph.service.MSGraphService;
import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import ca.bc.gov.ag.efax.mail.util.FileUtils;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;

@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SentMessageRepository sentMessageRepository;

    private MSGraphService gService;

    public EmailServiceImpl(SentMessageRepository sentMessageRepository, MSGraphService gService) {
        this.sentMessageRepository = sentMessageRepository;
        this.gService = gService;
    }

    @Override
	public List<GmailMessage> getInboxEmails() throws Exception {
		logger.trace("Retrieving inbox emails");

		MessageCollectionResponse responses = gService.GetMessages();

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(Message.class, GmailMessage.class).addMappings(mapper -> {
			// Body content is expected as text. See MSGraphServiceImpl.java Ln: 84
			mapper.map(src -> src.getBody().getContent(), GmailMessage::setBody);
			mapper.map(src -> src.getSubject(), GmailMessage::setSubject);
		});

		List<GmailMessage> o = new ArrayList<GmailMessage>();
		for (Message message : responses.getValue()) {
			GmailMessage e = modelMapper.map(message, GmailMessage.class);
			o.add(e);
		}

		return o;
	}   
  
	@Override
	public void deleteEmail(String id) throws Exception {
		
		logger.trace("Deleting email with id: " + id);
		gService.deleteMessage(id);
	} 

    @Override
    public void sendMessage(final MailMessage mailMessage) {
        try {
            // Store the UUID of this message in the redis queue. To avoid race conditions, this is stored now (before the actual sending of the
            // message) and removed if there is an error.
            SentMessage sentMessage = new SentMessage(mailMessage.getUuid(), mailMessage.getJobId(), new Date());
            logger.debug("Adding message to redis queue: " + sentMessage.getUuid());
            sentMessageRepository.save(sentMessage);
            
            processMessage(mailMessage);
            
        } catch (Exception e) {
        	
            logger.debug("Removing message from redis queue: " + mailMessage.getUuid());
            sentMessageRepository.deleteById(mailMessage.getUuid());
            throw new FAXSendFault("Unknown Exception in class sendMessage", e);
        }
    }
    
    private boolean processMessage(MailMessage mailMessage) throws Exception {
    	
        try {
        	gService.sendMessage(mailMessage);
        	
        } finally {
            cleanupMessage(mailMessage);
        }

        return true;
    }

    /** Delete temp file attachment(s) */
    private void cleanupMessage(MailMessage msg) {
        try {
            List<String> attachments = msg.getAttachments();
            if (attachments != null) {
                for (int i = 0; i < attachments.size(); i++) {
                    FileUtils.deleteTempFile(msg, i);
                }
            }
        } catch (Exception e) {
            // suppress exception propagation to quietly clean - just log the error.
            logger.error("Could not cleanup mailMessage attachments", e);
        }
    }
}
