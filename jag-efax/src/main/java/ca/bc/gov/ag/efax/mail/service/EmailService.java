package ca.bc.gov.ag.efax.mail.service;

import java.util.List;

import ca.bc.gov.ag.efax.mail.model.MailMessage;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

public interface EmailService {

    /**
     * Returns a collection of efax emails from the inbox folder.
     *
     * @return
     * @throws Exception 
     */
    List<EmailMessage> getInboxEmails() throws Exception;

    /**
     * Moves the specified email to the "Deleted Items" folder
     * @param emailMessage
     * @throws Exception 
     */
    void deleteEmail(EmailMessage emailMessage) throws Exception;
    
    /**
     * Sends an email (including any attachments) using Microsoft Exchange.
     * 
     * @param mailMessage
     */
    public void sendMessage(final MailMessage mailMessage);
    
}
