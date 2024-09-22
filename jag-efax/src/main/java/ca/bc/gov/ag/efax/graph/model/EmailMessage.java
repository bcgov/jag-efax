package ca.bc.gov.ag.efax.graph.model;

public class EmailMessage {
	
	private String subject;
	private String body;
	private String contentType; 
	
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
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	} 

}
