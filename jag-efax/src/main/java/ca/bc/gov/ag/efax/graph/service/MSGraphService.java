/**
 * 
 */
package ca.bc.gov.ag.efax.graph.service;

import com.microsoft.graph.models.MessageCollectionResponse;

import ca.bc.gov.ag.efax.mail.model.MailMessage;


/**
 * MS Graph Service Interface
 * 
 * @author 176899
 *
 */
public interface MSGraphService {
	
	public MessageCollectionResponse GetMessages() throws Exception;
	public void sendMessage(MailMessage mailMessage) throws Exception; 
	public void deleteMessage(String id) throws Exception; 
	public String getPasswordCredentialsExpiryDate(); 
	public String getApplicationName();
	
}
