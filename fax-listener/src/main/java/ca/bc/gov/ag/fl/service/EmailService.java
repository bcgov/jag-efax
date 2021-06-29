package ca.bc.gov.ag.fl.service;

import java.util.List;

import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

public interface EmailService {

    /**
     * Returns a collection of efax emails from the inbox folder.
     *
     * @return
     * @throws Exception 
     */
    List<EmailMessage> getEfaxInboxEmails() throws Exception;
    
}
