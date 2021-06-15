package ca.bc.gov.ag.mail;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

public class MailMessage {

	@NotEmpty(message = "Property 'uuid' must not be empty")
	private String uuid;
	@NotEmpty(message = "Property 'to' must not be empty")
	private String to;
	@NotEmpty(message = "Property 'subject' must not be empty")
	private String subject;
	private String body;
	private List<String> attachments;

	public MailMessage() {
	}
	
	public MailMessage(final String uuid) {
		this.uuid = uuid;
		this.to = "";
		this.subject = "";
		this.body = "";
		this.attachments = new ArrayList<String>();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

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

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	public void cleanup() {
		attachments.clear();
	}

}
