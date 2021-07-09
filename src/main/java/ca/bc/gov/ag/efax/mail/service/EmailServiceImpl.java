package ca.bc.gov.ag.efax.mail.service;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.bc.gov.ag.efax.mail.config.ExchangeProperties;
import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import ca.bc.gov.ag.efax.mail.util.FileUtils;
import ca.bc.gov.ag.efax.pdf.service.PdfService;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.SortDirection;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;

@Service
//FIXME: techdebt - codeclimate reports this class is too large (270 lines out of max 250)
public class EmailServiceImpl implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExchangeServiceFactory exchangeServiceFactory;

    @Autowired
    private ExchangeProperties exchangeProperties;

    @Autowired
    private SentMessageRepository sentMessageRepository;

    @Autowired
    private PdfService pdfService;

    @Value(value = "${exchange.poller.filter}")
    private String filter;

    @Override
    public List<EmailMessage> getInboxEmails() throws Exception {
        logger.trace("Retrieving inbox emails");

        ItemView view = new ItemView(Integer.MAX_VALUE);
        view.getOrderBy().add(ItemSchema.DateTimeReceived, SortDirection.Ascending);

        ExchangeService exchangeService = exchangeServiceFactory.createService();
        FindItemsResults<Item> emails = exchangeService.findItems(WellKnownFolderName.Inbox, filter, view);

        if (!emails.getItems().isEmpty()) {
            exchangeService.loadPropertiesForItems(emails, PropertySet.FirstClassProperties);
        }

        List<EmailMessage> emailMessages = emails.getItems().stream().map(item -> (EmailMessage) item).collect(Collectors.toList());
        logger.trace("Retrieved {} emails", emailMessages.size());
        return emailMessages;
    }

    @Override
    public void deleteEmail(EmailMessage emailMessage) throws Exception {
        ExchangeService exchangeService = exchangeServiceFactory.createService();
        exchangeService.deleteItem(emailMessage.getId(), DeleteMode.MoveToDeletedItems, null, null);
    }

    @Override
    public void sendMessage(final MailMessage mailMessage) {
        try {
            // Store the UUID of this message in the redis queue. To avoid race conditions, this is stored now (before the actual sending of the
            // message) and removed if there is an error.
            SentMessage sentMessage = new SentMessage(mailMessage.getUuid(), mailMessage.getJobId(), new Date());
            sentMessageRepository.save(sentMessage);

            processMessage(mailMessage);
        } catch (Exception e) {
            sentMessageRepository.deleteById(mailMessage.getUuid());
            throw new FAXSendFault("Unknown Exception in class sendMessage", e);
        }
    }

    private boolean processMessage(MailMessage mailMessage) throws Exception {
        try {
            ExchangeService service = exchangeServiceFactory.createService();

            EmailMessage message = new EmailMessage(service);
            message.getToRecipients().add(new EmailAddress(mailMessage.getTo()));
            message.setSubject(mailMessage.getSubject());
            message.setBody(MessageBody.getMessageBodyFromText(mailMessage.getBody()));

            List<String> attachmentURLs = mailMessage.getAttachments();
            for (int i = 0; i < attachmentURLs.size(); i++) {
                String url = attachmentURLs.get(i);
                String fileName = FileUtils.getTempFilename(mailMessage, i);
                File file = readFileFromURL(url, fileName);
                message.getAttachments().addFileAttachment(fileName, FileUtils.fileToByteArray(file));
            }

            message.sendAndSaveCopy(WellKnownFolderName.SentItems);
        } finally {
            cleanupMessage(mailMessage);
        }

        return true;
    }

    /** Delete temp file attachment(s) */
    private void cleanupMessage(MailMessage msg) {
        try {
            List<String> attachments = msg.getAttachments();
            for (int i = 0; i < attachments.size(); i++) {
                FileUtils.deleteTempFile(msg, i);
            }
        } catch (Exception e) {
            // suppress exception propagation to quietly clean - just log the error.
            logger.error("Could not cleanup mailMessage attachments", e);
        }
    }

    private File readFileFromURL(final String url, final String fileName) throws Exception {
        InputStream inputStream = null;

        try {
            String path = exchangeProperties.getTempDirectory() + fileName;

            // try first to flatten the PDF
            File file = pdfService.flattenPdf(url, path);

            // if unsuccessful, simply download the file as is
            if (file == null) {
                // Open the URL and get metadata
                inputStream = new URL(url).openStream();
                Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
                file = new File(path);
            }

            return file;
        } finally {
            if (inputStream != null)
                IOUtils.closeQuietly(inputStream);
        }
    }

}
