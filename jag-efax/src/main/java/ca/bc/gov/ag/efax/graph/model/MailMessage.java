package ca.bc.gov.ag.efax.graph.model;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public class MailMessage {
	
	//TODO - marked for delete after full testing. 

    @NotEmpty(message = "Property 'uuid' must not be empty")
    private String uuid;
    @NotEmpty(message = "Property 'jobId' must not be empty")
    private String jobId;
    @NotEmpty(message = "Property 'to' must not be empty")
    private String to;
    @NotEmpty(message = "Property 'subject' must not be empty")
    private String subject;
    private String body;
    private List<String> attachments;

    public MailMessage() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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

}
