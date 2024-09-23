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

    //@Autowired
    //private ExchangeServiceFactory exchangeServiceFactory;

    //@Autowired
    //private ExchangeProperties exchangeProperties;

    @Autowired
    private SentMessageRepository sentMessageRepository;

    //@Autowired
    //private PdfService pdfService;
    
    @Autowired
    private MSGraphService gService;
    
     
  @Override
	public List<GmailMessage> getInboxEmails() throws Exception {
		logger.trace("Retrieving inbox emails");

		MessageCollectionResponse responses = gService.GetMessages();

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(Message.class, GmailMessage.class).addMappings(mapper -> {
			mapper.map(src -> src.getBodyPreview(), GmailMessage::setBody);
			mapper.map(src -> src.getSubject(), GmailMessage::setSubject);
		});

		List<GmailMessage> o = new ArrayList<GmailMessage>();
		for (Message message : responses.getValue()) {
			GmailMessage e = modelMapper.map(message, GmailMessage.class);
			o.add(e);
		}

		return o;
	}   
    
// TODO - clean up. 
// Original code used for Jusefaxd.     
//    @Override
//    public List<EmailMessage> getInboxEmails() throws Exception {
//        logger.trace("Retrieving inbox emails");
//
//        ItemView view = new ItemView(Integer.MAX_VALUE);
//        view.getOrderBy().add(ItemSchema.DateTimeReceived, SortDirection.Ascending);
//
//        ExchangeService exchangeService = exchangeServiceFactory.createService();
//        FindItemsResults<Item> emails = exchangeService.findItems(WellKnownFolderName.Inbox, view);
//
//        if (!emails.getItems().isEmpty()) {
//            exchangeService.loadPropertiesForItems(emails, PropertySet.FirstClassProperties);
//        }
//
//        List<EmailMessage> emailMessages = emails.getItems().stream().map(item -> (EmailMessage) item).collect(Collectors.toList());
//        logger.trace("Retrieved {} emails", emailMessages.size());
//        
//        
//        //sm
//        if (!emails.getItems().isEmpty()) {
//        	for (EmailMessage item: emailMessages) {
//        		System.out.println("itemSubject: " + item.getSubject());
//        		System.out.println("itemBody: " + item.getBody());
//        		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(EFaxGraphConstants.dateFormat);
//        		System.out.println("itemDateReceived: " + simpleDateFormat.format(item.getDateTimeReceived()) + "\n");
//        	}
//        }
//        
//        return emailMessages;
//    }
  
  
	@Override
	public void deleteEmail(String id) throws Exception {
		
		logger.trace("Deleting email with id: " + id);
		gService.deleteMessage(id);
	} 

// TODO - clean up 	
// Original code used for Jusefaxd. 	
//    @Override
//    public void deleteEmail(GmailMessage emailMessage) throws Exception {
//        ExchangeService exchangeService = exchangeServiceFactory.createService();
//        exchangeService.deleteItem(emailMessage.getId(), DeleteMode.MoveToDeletedItems, null, null);
//    	System.out.println("deleteEmail - Implement me!");
//    }

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

// TODO - clean up    
// Original code used for Jusefaxd.     
//    private boolean processMessage(MailMessage mailMessage) throws Exception {
//        try {
//            ExchangeService service = exchangeServiceFactory.createService();
//
//            EmailMessage message = new EmailMessage(service);
//            message.getToRecipients().add(new EmailAddress(mailMessage.getTo()));
//            message.setSubject(mailMessage.getSubject());
//            message.setBody(MessageBody.getMessageBodyFromText(mailMessage.getBody()));
//
//            List<String> attachmentURLs = mailMessage.getAttachments();
//            if (attachmentURLs != null) {
//                for (int i = 0; i < attachmentURLs.size(); i++) {
//                    String url = attachmentURLs.get(i);
//                    String fileName = FileUtils.getTempFilename(mailMessage, i);
//                    File file = readFileFromURL(new URL(url), fileName, mailMessage.getJobId());
//                    message.getAttachments().addFileAttachment(fileName, FileUtils.fileToByteArray(file));
//                }
//            }
//            
//            if (exchangeProperties.getSaveInSent()) {
//                message.sendAndSaveCopy(WellKnownFolderName.SentItems);
//            }
//            else {
//                message.send();
//            }
//        } finally {
//            cleanupMessage(mailMessage);
//        }
//
//        return true;
//    }
    
    
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

// TODO - clean up    
//    private File readFileFromURL(final URL url, final String fileName, String jobId) throws Exception {
//        InputStream inputStream = null;
//
//        try {
//            String path = exchangeProperties.getTempDirectory() + fileName;
//            
//            // try first to flatten the PDF
//            File file = pdfService.flattenPdf(url, path, jobId);
//
//            // if unsuccessful, simply download the file as is
//            if (file == null) {
//                logger.info("PDF Flattening: jobId {} not flattened, using original file.", jobId);
//                // Open the URL and get metadata
//                inputStream = url.openStream();
//                Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
//                file = new File(path);
//            }
//
//            return file;
//        } finally {
//            if (inputStream != null)
//                IOUtils.closeQuietly(inputStream);
//        }
//    }

}
