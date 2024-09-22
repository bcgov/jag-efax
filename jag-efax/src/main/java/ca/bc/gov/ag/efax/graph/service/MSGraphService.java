/**
 * 
 */
package ca.bc.gov.ag.efax.graph.service;

import com.microsoft.graph.models.MessageCollectionResponse;

import ca.bc.gov.ag.efax.graph.model.EmailMessage;
import ca.bc.gov.ag.efax.graph.model.MailMessage;

/**
 * MS Graph Service Interface
 * 
 * @author 176899
 *
 */
public interface MSGraphService {
	
	public MessageCollectionResponse GetMessages() throws Exception;
	public void sendMessage(MailMessage mailMessage, boolean saveToSentItems) throws Exception; 
	public void deleteMessage(EmailMessage emailMessage) throws Exception; 
	public String getPasswordCredentialsExpiryDate(); 
	
}
