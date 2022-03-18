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
    private int snumPages;
    private String attachment;
    private String extension1;
    private String extension2;
    private String fromFaxNumber;
    private String fromPhoneNumber;
    private String documentStatus;
    private String documentStatusDate;
    private String receiveFaxCoverPageYn = "Y";
    private String endpoint;
    private String username;
    private String password;
    private String faxCoverSheetHtml;
    private String documentStatusHtmlFragment;

    protected ClientRequest() {
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

    public int getSnumPages() {
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

    public String getEndpoint() {
        return endpoint;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setWsdlEndpoint(String wsdlEndpoint) {
        this.wsdlEndpoint = wsdlEndpoint;
    }

    public void setWsdlUsername(String wsdlUsername) {
        this.wsdlUsername = wsdlUsername;
    }

    public void setWsdlPassword(String wsdlPassword) {
        this.wsdlPassword = wsdlPassword;
    }

    public void setCallbackEndpoint(String callbackEndpoint) {
        this.callbackEndpoint = callbackEndpoint;
    }

    public void setCallbackUsername(String callbackUsername) {
        this.callbackUsername = callbackUsername;
    }

    public void setCallbackPassword(String callbackPassword) {
        this.callbackPassword = callbackPassword;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setSdateTime(String sdateTime) {
        this.sdateTime = sdateTime;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public void setSchannel(String schannel) {
        this.schannel = schannel;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public void setSnumPages(int snumPages) {
        this.snumPages = snumPages;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public void setExtension1(String extension1) {
        this.extension1 = extension1;
    }

    public void setExtension2(String extension2) {
        this.extension2 = extension2;
    }

    public void setFromFaxNumber(String fromFaxNumber) {
        this.fromFaxNumber = fromFaxNumber;
    }

    public void setFromPhoneNumber(String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public void setDocumentStatusDate(String documentStatusDate) {
        this.documentStatusDate = documentStatusDate;
    }

    public void setReceiveFaxCoverPageYn(String receiveFaxCoverPageYn) {
        this.receiveFaxCoverPageYn = receiveFaxCoverPageYn;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFaxCoverSheetHtml(String faxCoverSheetHtml) {
        this.faxCoverSheetHtml = faxCoverSheetHtml;
    }

    public void setDocumentStatusHtmlFragment(String documentStatusHtmlFragment) {
        this.documentStatusHtmlFragment = documentStatusHtmlFragment;
    }
}
