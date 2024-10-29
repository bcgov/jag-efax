package ca.bc.gov.ag.efax.graph.model;


/**
 * Equivalent of microsoft.exchange.webservices.data.core.service.item.EmailMessage
 * used by the former Microsoft ExchangeService client API. 
 */
public class GmailMessage {
	
	private String subject;
	private String body;
	private String id; 
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	} 
}
