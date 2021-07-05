package ca.bc.gov.ag.efax.mail.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.axis.AxisFault;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.schemas.exchange.services._2006.messages.ArrayOfResponseMessagesType;
import com.microsoft.schemas.exchange.services._2006.messages.AttachmentInfoResponseMessageType;
import com.microsoft.schemas.exchange.services._2006.messages.CreateAttachmentType;
import com.microsoft.schemas.exchange.services._2006.messages.CreateItemType;
import com.microsoft.schemas.exchange.services._2006.messages.ExchangeServiceBindingStub;
import com.microsoft.schemas.exchange.services._2006.messages.ItemInfoResponseMessageType;
import com.microsoft.schemas.exchange.services._2006.messages.SendItemType;
import com.microsoft.schemas.exchange.services._2006.messages.holders.CreateAttachmentResponseTypeHolder;
import com.microsoft.schemas.exchange.services._2006.messages.holders.CreateItemResponseTypeHolder;
import com.microsoft.schemas.exchange.services._2006.messages.holders.SendItemResponseTypeHolder;
import com.microsoft.schemas.exchange.services._2006.types.ArrayOfRecipientsType;
import com.microsoft.schemas.exchange.services._2006.types.BodyType;
import com.microsoft.schemas.exchange.services._2006.types.BodyTypeType;
import com.microsoft.schemas.exchange.services._2006.types.DistinguishedFolderIdNameType;
import com.microsoft.schemas.exchange.services._2006.types.DistinguishedFolderIdType;
import com.microsoft.schemas.exchange.services._2006.types.DistinguishedPropertySetType;
import com.microsoft.schemas.exchange.services._2006.types.EmailAddressType;
import com.microsoft.schemas.exchange.services._2006.types.ExchangeVersionType;
import com.microsoft.schemas.exchange.services._2006.types.ExtendedPropertyType;
import com.microsoft.schemas.exchange.services._2006.types.FileAttachmentType;
import com.microsoft.schemas.exchange.services._2006.types.ItemIdType;
import com.microsoft.schemas.exchange.services._2006.types.MailboxCultureType;
import com.microsoft.schemas.exchange.services._2006.types.MapiPropertyTypeType;
import com.microsoft.schemas.exchange.services._2006.types.MessageDispositionType;
import com.microsoft.schemas.exchange.services._2006.types.MessageType;
import com.microsoft.schemas.exchange.services._2006.types.NonEmptyArrayOfAllItemsType;
import com.microsoft.schemas.exchange.services._2006.types.NonEmptyArrayOfAttachmentsType;
import com.microsoft.schemas.exchange.services._2006.types.NonEmptyArrayOfBaseItemIdsType;
import com.microsoft.schemas.exchange.services._2006.types.PathToExtendedFieldType;
import com.microsoft.schemas.exchange.services._2006.types.RequestServerVersion;
import com.microsoft.schemas.exchange.services._2006.types.ResponseClassType;
import com.microsoft.schemas.exchange.services._2006.types.SensitivityChoicesType;
import com.microsoft.schemas.exchange.services._2006.types.TargetFolderIdType;
import com.microsoft.schemas.exchange.services._2006.types.holders.ServerVersionInfoHolder;

import ca.bc.gov.ag.efax.mail.config.ExchangeProperties;
import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import ca.bc.gov.ag.efax.mail.util.FileUtils;
import ca.bc.gov.ag.efax.pdf.service.PdfService;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.jag.ews.proxy.ExchangeWebServiceClient;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.SortDirection;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
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
    public List<EmailMessage> getEfaxInboxEmails() throws Exception {    
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
            ExchangeWebServiceClient service = exchangeServiceFactory.createClient();
            ExchangeServiceBindingStub serviceStub = service.getServiceStub();
            
            // Create a Microsoft Exchange email item
            CreateItemResponseTypeHolder createItemResult = exchangeCreateItem(serviceStub, mailMessage);
    
            // Add attachments to the Microsoft Exchange item
            ItemIdType parentItemId = exchangeAddAttachments(serviceStub, createItemResult, mailMessage);
           
            // Send the Microsoft Exchange email item
            SendItemResponseTypeHolder sendItemResult = exchangeSendItem(serviceStub, parentItemId);
    
            ArrayOfResponseMessagesType sentResp = sendItemResult.value.getResponseMessages();
            ItemInfoResponseMessageType sentRespMsg = sentResp.getCreateItemResponseMessage();
            if (sentRespMsg != null && !ResponseClassType.Success.equals(sentRespMsg.getResponseClass())) {
                throw new Exception("Exception sending message. " + sentRespMsg.getMessageText());
            }
        } catch (AxisFault e) {
            throw new Exception("Invalid Exchange credentials", e);
        } catch (Exception e) {
            throw new Exception("Unknown Exception in class processMessage", e);
        } finally {
            cleanupMessage(mailMessage);
        }
    
        return true;
    }

    private CreateItemResponseTypeHolder exchangeCreateItem(ExchangeServiceBindingStub serviceStub, MailMessage mailMessage) throws RemoteException {
                
        NonEmptyArrayOfAllItemsType items = new NonEmptyArrayOfAllItemsType();
        items.setMessage(getMessageType(mailMessage));
        
        CreateItemType request = new CreateItemType();
        request.setMessageDisposition(MessageDispositionType.SaveOnly);
        request.setItems(items);
    
        MailboxCultureType mailboxCultureType = new MailboxCultureType("en-US");
        
        RequestServerVersion requestVersion = new RequestServerVersion();
        requestVersion.setVersion(ExchangeVersionType.Exchange2013);
        
        CreateItemResponseTypeHolder createItemResult = new CreateItemResponseTypeHolder();
        
        ServerVersionInfoHolder serverVersion = new ServerVersionInfoHolder();
        
        serviceStub.createItem(request, null, mailboxCultureType, requestVersion, null, createItemResult, serverVersion);
        
        return createItemResult;
    }

    private ItemIdType exchangeAddAttachments(ExchangeServiceBindingStub serviceStub, CreateItemResponseTypeHolder createItemResult, MailMessage mailMessage) throws Exception {
        // FIXME: techdebt - codeclimate reports this method is too complex (complexity of 13 out of 5).
        // FIXME: techdebt - codeclimate reports this method is too large (45 lines out of max 25).
        ItemIdType parentItemId = new ItemIdType();
    
        ArrayOfResponseMessagesType resp = createItemResult.value.getResponseMessages();
        ItemInfoResponseMessageType respMsg = resp.getCreateItemResponseMessage();
        if (ResponseClassType.Success.equals(respMsg.getResponseClass())) {
            List<String> attachmentList = mailMessage.getAttachments();
            if (attachmentList.size() > 0) {
                for (int i = 0; i < attachmentList.size(); i++) {
                    String attachment = attachmentList.get(i);
                    String tempFilename = FileUtils.getTempFilename(mailMessage, i);
                    File attachmentFile = readFileFromURL(attachment, tempFilename);
    
                    FileAttachmentType fileAttachment = new FileAttachmentType();
                    fileAttachment.setName(tempFilename);
                    fileAttachment.setContentType("application/pdf");
                    fileAttachment.setContent(FileUtils.fileToByteArray(attachmentFile));
    
                    NonEmptyArrayOfAttachmentsType attachments = new NonEmptyArrayOfAttachmentsType();
                    attachments.setFileAttachment(fileAttachment);
    
                    CreateAttachmentType attachmentRequest = new CreateAttachmentType();
                    attachmentRequest.setAttachments(attachments);
                    attachmentRequest.setParentItemId(respMsg.getItems().getMessage().getItemId());
    
                    MailboxCultureType mailboxCultureType = new MailboxCultureType("en-US");
    
                    RequestServerVersion requestVersion = new RequestServerVersion();
                    requestVersion.setVersion(ExchangeVersionType.Exchange2013);
                    
                    CreateAttachmentResponseTypeHolder createAttachmentResult = new CreateAttachmentResponseTypeHolder();
    
                    ServerVersionInfoHolder serverVersion = new ServerVersionInfoHolder();
                    
                    serviceStub.createAttachment(attachmentRequest, null, mailboxCultureType, requestVersion, null, createAttachmentResult, serverVersion);
    
                    ArrayOfResponseMessagesType attachmentResp = createAttachmentResult.value.getResponseMessages();
                    AttachmentInfoResponseMessageType attachRespMsg = attachmentResp
                            .getCreateAttachmentResponseMessage();
                    if (ResponseClassType.Success.equals(attachRespMsg.getResponseClass())) {
                        parentItemId.setChangeKey(attachRespMsg.getAttachments().getFileAttachment()
                                .getAttachmentId().getRootItemChangeKey());
                        parentItemId.setId(attachRespMsg.getAttachments().getFileAttachment().getAttachmentId()
                                .getRootItemId());
                    } else {
                        throw new Exception("Exception putting attachments on message. " + respMsg.getMessageText());
                    }
                }
            } else {
                parentItemId.setId(respMsg.getItems().getMessage().getItemId().getId());
                parentItemId.setChangeKey(respMsg.getItems().getMessage().getItemId().getChangeKey());
            }
        } else {
            throw new Exception("Exception saving email draft. " + respMsg.getMessageText());
        }
        
        return parentItemId;
    }

    private SendItemResponseTypeHolder exchangeSendItem(ExchangeServiceBindingStub serviceStub, ItemIdType parentItemId) throws RemoteException {
        NonEmptyArrayOfBaseItemIdsType itemIds = new NonEmptyArrayOfBaseItemIdsType();
        itemIds.setItemId(parentItemId);
    
        SendItemType sendRequest = new SendItemType();
        sendRequest.setItemIds(itemIds);
        sendRequest.setSavedItemFolderId(getSavedItemFolderId());
        sendRequest.setSaveItemToFolder(exchangeProperties.getSaveInSent());
    
        MailboxCultureType mailboxCultureType = new MailboxCultureType("en-US");
    
        RequestServerVersion requestVersion = new RequestServerVersion();
        requestVersion.setVersion(ExchangeVersionType.Exchange2013);
    
        SendItemResponseTypeHolder sendItemResult = new SendItemResponseTypeHolder(); 
    
        ServerVersionInfoHolder serverVersion = new ServerVersionInfoHolder();
        
        serviceStub.sendItem(sendRequest, null, mailboxCultureType, requestVersion, sendItemResult, serverVersion);
        
        return sendItemResult;
    }

    /** Delete temp attachment(s) file(s) */
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

    private File readFileFromURL(final String url, final String outFilename) throws Exception {
        // FIXME: techdebt - codeclimate reports this method is too complex (complexity of 10 out of 5).
        // FIXME: techdebt - codeclimate reports this method is too large (29 lines out of max 25).
        URLConnection uc = null;
        FileOutputStream fos = null;
        
        try {
            // Open the URL and get metadata
            URL u = new URL(url);
            uc = u.openConnection();
            
            // Open the outgoing stream and file
            File outFile = new File(exchangeProperties.getTempDirectory() + outFilename);
            fos = new FileOutputStream(outFile);

            boolean isFlattened = pdfService.flattenPdf(url, fos);
            
            if (!isFlattened) {
                try {
                    IOUtils.copy(uc.getInputStream(), fos);
                    fos.flush();
                } catch (Exception e) {}
            }

            return outFile;
        } catch (MalformedURLException mue) {
            throw new Exception("URL Exception in class readFileFromURL", mue);
        } catch (SecurityException se) {
            throw new Exception("Security Exception in class readFileFromURL", se);
        } catch (IOException ie) {
            throw new Exception("IO Exception in class readFileFromURL", ie);
        } catch (Exception e) {
            throw new Exception("Unknown Exception in class readFileFromURL", e);
        } finally {
            if (uc != null && uc.getInputStream() != null)
                IOUtils.closeQuietly(uc.getInputStream());
            if (fos != null)
                IOUtils.closeQuietly(fos);
        }
    }

    private TargetFolderIdType getSavedItemFolderId() {
        DistinguishedFolderIdType distinguishedFolderId = new DistinguishedFolderIdType();
        distinguishedFolderId.setId(DistinguishedFolderIdNameType.sentitems);
        TargetFolderIdType savedItemFolderId = new TargetFolderIdType();
        savedItemFolderId.setDistinguishedFolderId(distinguishedFolderId);
        return savedItemFolderId;
    }

    private MessageType getMessageType(MailMessage mailMessage) {
        BodyType body = new BodyType();
        body.setBodyType(BodyTypeType.HTML);
        body.set_value(mailMessage.getBody());

        EmailAddressType recipientMailbox = new EmailAddressType();
        recipientMailbox.setEmailAddress(mailMessage.getTo());

        ArrayOfRecipientsType toRecipients = new ArrayOfRecipientsType();
        toRecipients.setMailbox(recipientMailbox);

        PathToExtendedFieldType extendedFieldURI = new PathToExtendedFieldType();
        extendedFieldURI.setDistinguishedPropertySetId(DistinguishedPropertySetType.InternetHeaders);
        extendedFieldURI.setPropertyName("X-Mailer");
        extendedFieldURI.setPropertyType(MapiPropertyTypeType.String);

        ExtendedPropertyType[] extendedProperty = new ExtendedPropertyType[1];
        extendedProperty[0] = new ExtendedPropertyType();
        extendedProperty[0].setExtendedFieldURI(extendedFieldURI);
        extendedProperty[0].setValue("MailService");

        MessageType messageType = new MessageType();
        messageType.setSubject(mailMessage.getSubject());
        messageType.setBody(body);
        messageType.setToRecipients(toRecipients);
        messageType.setSensitivity(SensitivityChoicesType.Normal);
        messageType.setExtendedProperty(extendedProperty);
        
        return messageType;
    }
    
}
