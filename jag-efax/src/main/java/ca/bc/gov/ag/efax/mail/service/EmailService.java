package ca.bc.gov.ag.efax.mail.service;

import java.util.List;

import ca.bc.gov.ag.efax.graph.model.GmailMessage;
import ca.bc.gov.ag.efax.mail.model.MailMessage;

public interface EmailService {

    /**
     * Returns a collection of efax emails from the inbox folder.
     *
     * @return
     * @throws Exception 
     */
    List<GmailMessage> getInboxEmails() throws Exception;

    /**
     * Moves the specified email to the "Deleted Items" folder
     * @param emailMessage
     * @throws Exception 
     */
    void deleteEmail(String id) throws Exception;
    
    /**
     * Sends an email (including any attachments) using Microsoft Exchange.
     * 
     * @param mailMessage
     */
    public void sendMessage(final MailMessage mailMessage);
    
}
