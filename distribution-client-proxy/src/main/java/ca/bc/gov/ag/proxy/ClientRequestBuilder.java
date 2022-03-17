package ca.bc.gov.ag.proxy;

import java.util.StringJoiner;

public class ClientRequestBuilder {
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
    private String receiveFaxCoverPageYn;


    public ClientRequest build() {
        validate();
        return null;
//        return new ClientRequest();
    }

    private void validate() {

    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ClientRequestBuilder.class.getSimpleName() + "[", "]")
                .add("wsdlEndpoint='" + wsdlEndpoint + "'")
                .add("wsdlUsername='" + wsdlUsername + "'")
                .add("wsdlPassword='" + wsdlPassword + "'")
                .add("callbackEndpoint='" + callbackEndpoint + "'")
                .add("callbackUsername='" + callbackUsername + "'")
                .add("callbackPassword='" + callbackPassword + "'")
                .add("from='" + from + "'")
                .add("to='" + to + "'")
                .add("jobId='" + jobId + "'")
                .add("sdateTime='" + sdateTime + "'")
                .add("timeout='" + timeout + "'")
                .add("schannel='" + schannel + "'")
                .add("transport='" + transport + "'")
                .add("subject='" + subject + "'")
                .add("fileNumber='" + fileNumber + "'")
                .add("snumPages='" + snumPages + "'")
                .add("attachment='" + attachment + "'")
                .add("extension1='" + extension1 + "'")
                .add("extension2='" + extension2 + "'")
                .add("fromFaxNumber='" + fromFaxNumber + "'")
                .add("fromPhoneNumber='" + fromPhoneNumber + "'")
                .add("documentStatus='" + documentStatus + "'")
                .add("documentStatusDate='" + documentStatusDate + "'")
                .add("receiveFaxCoverPageYn='" + receiveFaxCoverPageYn + "'")
                .toString();
    }

    public ClientRequestBuilder setWsdlEndpoint(String wsdlEndpoint) {
        this.wsdlEndpoint = wsdlEndpoint;
        return this;
    }

    public ClientRequestBuilder setWsdlUsername(String wsdlUsername) {
        this.wsdlUsername = wsdlUsername;
        return this;
    }

    public ClientRequestBuilder setWsdlPassword(String wsdlPassword) {
        this.wsdlPassword = wsdlPassword;
        return this;
    }

    public ClientRequestBuilder setCallbackEndpoint(String callbackEndpoint) {
        this.callbackEndpoint = callbackEndpoint;
        return this;
    }

    public ClientRequestBuilder setCallbackUsername(String callbackUsername) {
        this.callbackUsername = callbackUsername;
        return this;
    }

    public ClientRequestBuilder setCallbackPassword(String callbackPassword) {
        this.callbackPassword = callbackPassword;
        return this;
    }

    public ClientRequestBuilder setFrom(String from) {
        this.from = from;
        return this;
    }

    public ClientRequestBuilder setTo(String to) {
        this.to = to;
        return this;
    }

    public ClientRequestBuilder setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public ClientRequestBuilder setSdateTime(String sdateTime) {
        this.sdateTime = sdateTime;
        return this;
    }

    public ClientRequestBuilder setTimeout(String timeout) {
        this.timeout = timeout;
        return this;
    }

    public ClientRequestBuilder setSchannel(String schannel) {
        this.schannel = schannel;
        return this;
    }

    public ClientRequestBuilder setTransport(String transport) {
        this.transport = transport;
        return this;
    }

    public ClientRequestBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public ClientRequestBuilder setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
        return this;
    }

    public ClientRequestBuilder setSnumPages(String snumPages) {
        this.snumPages = snumPages;
        return this;
    }

    public ClientRequestBuilder setAttachment(String attachment) {
        this.attachment = attachment;
        return this;
    }

    public ClientRequestBuilder setExtension1(String extension1) {
        this.extension1 = extension1;
        return this;
    }

    public ClientRequestBuilder setExtension2(String extension2) {
        this.extension2 = extension2;
        return this;
    }

    public ClientRequestBuilder setFromFaxNumber(String fromFaxNumber) {
        this.fromFaxNumber = fromFaxNumber;
        return this;
    }

    public ClientRequestBuilder setFromPhoneNumber(String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
        return this;
    }

    public ClientRequestBuilder setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
        return this;
    }

    public ClientRequestBuilder setDocumentStatusDate(String documentStatusDate) {
        this.documentStatusDate = documentStatusDate;
        return this;
    }

    public ClientRequestBuilder setReceiveFaxCoverPageYn(String receiveFaxCoverPageYn) {
        this.receiveFaxCoverPageYn = receiveFaxCoverPageYn;
        return this;
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
