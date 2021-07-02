package ca.bc.gov.ag.efax.mail.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.apache.axis.AxisFault;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import ca.bc.gov.ag.efax.mail.util.StringUtils;
import ca.bc.gov.ag.efax.pdf.service.PdfService;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.jag.ews.proxy.ExchangeWebServiceClient;

@Service
// FIXME: techdept - codeclimate reports this class is too large.
public class MailService {    
    
    private Logger logger = LoggerFactory.getLogger(MailService.class);
    
    @Autowired
    private ExchangeProperties exchangeProperties;

    @Autowired
    private SentMessageRepository sentMessageRepository;

    @Autowired
    private ExchangeServiceFactory exchangeServiceFactory;
    
    @Autowired
    private PdfService pdfService;
    
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

    // Post processing cleanup
    // Delete temp attachment(s) file(s)
    // Remove uuid-message key-value pair from messageMap
    private void cleanupMessage(MailMessage msg) throws Exception {
        // FIXME: techdept - codeclimate reports this class is too complex.
        try {
            // Delete temp attachment file(s)
            List<String> attachmentList = msg.getAttachments();
            for (int i = 0; i < attachmentList.size(); i++) {
                String tempFilename = "tmp_attach_" + StringUtils.normalizeUUID(msg.getUuid()) + "_" + i + ".pdf";
                File tempFile = new File(exchangeProperties.getTempDirectory() + tempFilename);
                if (!tempFile.exists()) {
                    throw new Exception("File Does Not Exist");
                }
                if (!tempFile.delete()) {
                    throw new Exception("Unable to Delete file");
                }
            }
        } catch (SecurityException se) {
            throw new Exception("Security Exception in cleanupMessage", se);
        } catch (Exception e) {
            throw new Exception("Unknown Exception in cleanupMessage", e);
        }

    }

    private File readFileFromURL(final String url, final String outFilename) throws Exception {
        try {
            // Open the URL and get metadata
            URL u = new URL(url);
            URLConnection uc = u.openConnection();
            
            // Open the outgoing stream and file
            File outFile = new File(exchangeProperties.getTempDirectory() + outFilename);
            FileOutputStream fos = new FileOutputStream(outFile);

            boolean isFlattened = pdfService.flattenPdf(url, fos);
            
            if (!isFlattened) {
                try {
                    IOUtils.copy(uc.getInputStream(), fos);
                    fos.flush();
                } finally {
                    IOUtils.closeQuietly(uc.getInputStream(), fos);
                }
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
            // NOP
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
            if (!ResponseClassType.Success.equals(sentRespMsg.getResponseClass())) {
                throw new Exception("Exception sending message. " + sentRespMsg.getMessageText());
            }
        } catch (AxisFault e) {
            throw new Exception("Invalid Exchange credentials", e);
        } catch (Exception e) {
            throw new Exception("Unknown Exception in class processMessage", e);
        } finally {
            try {
                cleanupMessage(mailMessage);
            } catch (Exception e) {
                // FIXME: techdept - this should not be an exception since it has nothing to do with whether a fax/email successfully sends or not.
                throw new Exception("Exception Cleaning up Temporary File");
            }
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
        ItemIdType parentItemId = new ItemIdType();

        ArrayOfResponseMessagesType resp = createItemResult.value.getResponseMessages();
        ItemInfoResponseMessageType respMsg = resp.getCreateItemResponseMessage();
        if (ResponseClassType.Success.equals(respMsg.getResponseClass())) {
            List<String> attachmentList = mailMessage.getAttachments();
            if (attachmentList.size() > 0) {
                for (int i = 0; i < attachmentList.size(); i++) {
                    String attachment = (String) attachmentList.get(i);
                    String tempFilename = "tmp_attach_" + StringUtils.normalizeUUID(mailMessage.getUuid()) + "_" + i + ".pdf";
                    File attachmentFile = readFileFromURL(attachment, tempFilename);

                    FileAttachmentType fileAttachment = new FileAttachmentType();
                    fileAttachment.setName(tempFilename);
                    fileAttachment.setContentType("application/pdf");
                    fileAttachment.setContent(fileToByteArray(attachmentFile));

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

    private byte[] fileToByteArray(File attachmentFile) throws Exception {
        ByteArrayOutputStream ous = null;
        InputStream ios = null;

        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(attachmentFile);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
            ous.flush();
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }
}
