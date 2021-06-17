package ca.bc.gov.ag.mail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.schemas.exchange.services._2006.messages.ArrayOfResponseMessagesType;
import com.microsoft.schemas.exchange.services._2006.messages.AttachmentInfoResponseMessageType;
import com.microsoft.schemas.exchange.services._2006.messages.CreateAttachmentType;
import com.microsoft.schemas.exchange.services._2006.messages.CreateItemType;
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

import ca.bc.gov.ag.exception.MailException;
import ca.bc.gov.ag.pdf.PdfService;
import ca.bc.gov.ag.util.StringUtils;
import ca.bc.gov.jag.ews.proxy.ExchangeWebServiceClient;

@Service
public class MailService {

	@Autowired
	private MailProperties mailProperties;
	
	@Autowired
	private PdfService pdfService;
	
	public void sendMessage(final String uuid, final String to, final String subject, final String body,
			final URI... attachments) throws MailException {

		MailMessage mailMessage = new MailMessage(uuid);
		mailMessage.setTo(to);
		mailMessage.setSubject(subject);
		mailMessage.setBody(body);

		if ((attachments != null) && (attachments.length > 0)) {
			for (int i = 0; i < attachments.length; i++) {
				mailMessage.getAttachments().add(attachments[i].toString());
			}
		}
		
		try {
			processMessage(mailMessage);
		} catch (Exception e) {
			throw new MailException("Unknown Exception in class sendMessage", e);
		}
	}

	// Post processing cleanup
	// Delete temp attachment(s) file(s)
	// Remove uuid-message key-value pair from messageMap
	private void cleanupMessage(MailMessage msg) throws Exception {
		try {
			// Delete temp attachment file(s)
			List<String> attachmentList = msg.getAttachments();
			for (int i = 0; i < attachmentList.size(); i++) {
				String tempFilename = "tmp_attach_" + StringUtils.normalizeUUID(msg.getUuid()) + "_" + i + ".pdf";
				File tempFile = new File(mailProperties.getTempDirectory() + tempFilename);
				if (!tempFile.exists()) {
					throw new Exception("File Does Not Exist");
				}
				if (!tempFile.delete()) {
					throw new Exception("Unable to Delete file");
				}
			}
			// Cleanup
			msg.cleanup();
		} catch (SecurityException se) {
			throw new MailException("Security Exception in class setMessage", se);
		} catch (Exception e) {
			throw new MailException("Unknown Exception in class setMessage", e);
		}

	}

	private File readFileFromURL(final String url, final String outFilename) throws MailException {
		try {
			// Open the URL and get metadata
			URL u = new URL(url);
			URLConnection uc = u.openConnection();
			
			// Open the outgoing stream and file
			File outFile = new File(mailProperties.getTempDirectory() + outFilename);
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
			throw new MailException("URL Exception in class readFileFromURL", mue);
		} catch (SecurityException se) {
			throw new MailException("Security Exception in class readFileFromURL", se);
		} catch (IOException ie) {
			throw new MailException("IO Exception in class readFileFromURL", ie);
		} catch (Exception e) {
			throw new MailException("Unknown Exception in class readFileFromURL", e);
		} finally {
			// NOP
		}
	}

	private boolean processMessage(MailMessage m) throws MailException {
		try {
			ExchangeWebServiceClient service;
			try {
				service = new ExchangeWebServiceClient(mailProperties.getExchangeWSEndpoint(),
						mailProperties.getUsername(), mailProperties.getPassword());
			} catch (ServiceException e) {
				System.out.println("ServiceException: ");
				e.printStackTrace(System.out);
				throw new MailException("ServiceException Exception in class processMessage", e);
			} catch (MalformedURLException e) {
				System.out.println("MalformedURLException: ");
				e.printStackTrace(System.out);
				throw new MailException("MalformedURLException Exception in class processMessage", e);
			}
			MailboxCultureType mailboxCultureType = new MailboxCultureType("en-US");
			ServerVersionInfoHolder serverVersion = new ServerVersionInfoHolder();
			RequestServerVersion requestVersion = new RequestServerVersion();
			requestVersion.setVersion(ExchangeVersionType.Exchange2013);

			BodyType body = new BodyType();
			body.setBodyType(BodyTypeType.HTML);
			body.set_value(m.getBody());

			EmailAddressType recipientMailbox = new EmailAddressType();
			recipientMailbox.setEmailAddress(m.getTo());

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

			MessageType message = new MessageType();
			message.setSubject(m.getSubject());
			message.setBody(body);
			message.setToRecipients(toRecipients);
			message.setSensitivity(SensitivityChoicesType.Normal);
			message.setExtendedProperty(extendedProperty);

			NonEmptyArrayOfAllItemsType items = new NonEmptyArrayOfAllItemsType();
			items.setMessage(message);

			CreateItemType request = new CreateItemType();
			request.setMessageDisposition(MessageDispositionType.SaveOnly);
			request.setItems(items);

			CreateItemResponseTypeHolder createItemResult = new CreateItemResponseTypeHolder();
			service.getServiceStub().createItem(request, null, mailboxCultureType, requestVersion, null,
					createItemResult, serverVersion);

			ItemIdType parentItemId = new ItemIdType();

			ArrayOfResponseMessagesType resp = createItemResult.value.getResponseMessages();
			ItemInfoResponseMessageType respMsg = resp.getCreateItemResponseMessage();
			if (ResponseClassType.Success.equals(respMsg.getResponseClass())) {
				List<String> attachmentList = m.getAttachments();
				if (attachmentList.size() > 0) {
					for (int i = 0; i < attachmentList.size(); i++) {
						String attachment = (String) attachmentList.get(i);
						String tempFilename = "tmp_attach_" + StringUtils.normalizeUUID(m.getUuid()) + "_" + i + ".pdf";
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

						CreateAttachmentResponseTypeHolder createAttachmentResult = new CreateAttachmentResponseTypeHolder();

						service.getServiceStub().createAttachment(attachmentRequest, null, mailboxCultureType,
								requestVersion, null, createAttachmentResult, serverVersion);

						ArrayOfResponseMessagesType attachmentResp = createAttachmentResult.value.getResponseMessages();
						AttachmentInfoResponseMessageType attachRespMsg = attachmentResp
								.getCreateAttachmentResponseMessage();
						if (ResponseClassType.Success.equals(attachRespMsg.getResponseClass())) {
							parentItemId.setChangeKey(attachRespMsg.getAttachments().getFileAttachment()
									.getAttachmentId().getRootItemChangeKey());
							parentItemId.setId(attachRespMsg.getAttachments().getFileAttachment().getAttachmentId()
									.getRootItemId());
						} else {
							throw new MailException(
									"Exception putting attachments on message. " + respMsg.getMessageText());
						}
					}
				} else {
					parentItemId.setId(respMsg.getItems().getMessage().getItemId().getId());
					parentItemId.setChangeKey(respMsg.getItems().getMessage().getItemId().getChangeKey());
				}
			} else {
				throw new MailException("Exception saving email draft. " + respMsg.getMessageText());
			}

			NonEmptyArrayOfBaseItemIdsType itemIds = new NonEmptyArrayOfBaseItemIdsType();
			itemIds.setItemId(parentItemId);

			DistinguishedFolderIdType distinguishedFolderId = new DistinguishedFolderIdType();
			distinguishedFolderId.setId(DistinguishedFolderIdNameType.sentitems);
			TargetFolderIdType savedItemFolderId = new TargetFolderIdType();
			savedItemFolderId.setDistinguishedFolderId(distinguishedFolderId);

			SendItemType sendRequest = new SendItemType();
			sendRequest.setItemIds(itemIds);
			sendRequest.setSavedItemFolderId(savedItemFolderId);

			if (mailProperties.getSaveInSent()) {
				sendRequest.setSaveItemToFolder(true);
			} else {
				sendRequest.setSaveItemToFolder(false);
			}
			SendItemResponseTypeHolder sendItemResult = new SendItemResponseTypeHolder();
			service.getServiceStub().sendItem(sendRequest, null, mailboxCultureType, requestVersion, sendItemResult,
					serverVersion);

			ArrayOfResponseMessagesType sentResp = sendItemResult.value.getResponseMessages();
			ItemInfoResponseMessageType sentRespMsg = sentResp.getCreateItemResponseMessage();
			if (ResponseClassType.Success.equals(respMsg.getResponseClass())) {
			} else {
				throw new MailException("Exception sending message. " + sentRespMsg.getMessageText());
			}
		} catch (Exception e) {
			throw new MailException("Unknown Exception in class processMessage", e);
		} finally {
			try {
				cleanupMessage(m);
			} catch (Exception e) {
				throw new MailException("Exception Cleaning up Temporary File");
			}
		}

		return true;
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
