package ca.bc.gov.ag.proxy;

public class ClientRequest {
    private String wsdlEndpoint;
    private String wsdlUsername;
    private String wsdlPassword;
    private String callbackEndpoint;
    private String callbackUsername;
    private String callbackPassword;
    private String from;
    private String to;
    private String jobId;
    private String sdateTime;
    private String timeout;
    private String schannel;
    private String transport;
    private String subject;
    private String fileNumber;
    private String snumPages;
    private String attachment;
    private String extension1;
    private String extension2;
    private String fromFaxNumber;
    private String fromPhoneNumber;
    private String documentStatus;
    private String documentStatusDate;
    private String receiveFaxCoverPageYn = "Y";

    public ClientRequest(String wsdlEndpoint, String wsdlUsername, String wsdlPassword, String callbackEndpoint, String callbackUsername, String callbackPassword, String from, String to, String jobId, String sdateTime, String timeout, String schannel, String transport, String subject, String fileNumber, String snumPages, String attachment, String extension1, String extension2, String fromFaxNumber, String fromPhoneNumber, String documentStatus, String documentStatusDate, String receiveFaxCoverPageYn) {
        this.wsdlEndpoint = wsdlEndpoint;
        this.wsdlUsername = wsdlUsername;
        this.wsdlPassword = wsdlPassword;
        this.callbackEndpoint = callbackEndpoint;
        this.callbackUsername = callbackUsername;
        this.callbackPassword = callbackPassword;
        this.from = from;
        this.to = to;
        this.jobId = jobId;
        this.sdateTime = sdateTime;
        this.timeout = timeout;
        this.schannel = schannel;
        this.transport = transport;
        this.subject = subject;
        this.fileNumber = fileNumber;
        this.snumPages = snumPages;
        this.attachment = attachment;
        this.extension1 = extension1;
        this.extension2 = extension2;
        this.fromFaxNumber = fromFaxNumber;
        this.fromPhoneNumber = fromPhoneNumber;
        this.documentStatus = documentStatus;
        this.documentStatusDate = documentStatusDate;
        this.receiveFaxCoverPageYn = receiveFaxCoverPageYn;

    }

    public String getWsdlEndpoint() {
        return wsdlEndpoint;
    }

    public String getWsdlUsername() {
        return wsdlUsername;
    }

    public String getWsdlPassword() {
        return wsdlPassword;
    }

    public String getCallbackEndpoint() {
        return callbackEndpoint;
    }

    public String getCallbackUsername() {
        return callbackUsername;
    }

    public String getCallbackPassword() {
        return callbackPassword;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getJobId() {
        return jobId;
    }

    public String getSdateTime() {
        return sdateTime;
    }

    public String getTimeout() {
        return timeout;
    }

    public String getSchannel() {
        return schannel;
    }

    public String getTransport() {
        return transport;
    }

    public String getSubject() {
        return subject;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public String getSnumPages() {
        return snumPages;
    }

    public String getAttachment() {
        return attachment;
    }

    public String getExtension1() {
        return extension1;
    }

    public String getExtension2() {
        return extension2;
    }

    public String getFromFaxNumber() {
        return fromFaxNumber;
    }

    public String getFromPhoneNumber() {
        return fromPhoneNumber;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public String getDocumentStatusDate() {
        return documentStatusDate;
    }

    public String getReceiveFaxCoverPageYn() {
        return receiveFaxCoverPageYn;
    }
}
