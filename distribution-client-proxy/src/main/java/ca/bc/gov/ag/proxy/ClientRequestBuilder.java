package ca.bc.gov.ag.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ca.bc.gov.ag.proxy.validation.ClientRequestValidator.validate;

public class ClientRequestBuilder {

    private static Logger logger = LoggerFactory.getLogger(ClientRequestBuilder.class);

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
    private int snumPages = 0;
    private String attachment;
    private String extension1;
    private String extension2;
    private String fromFaxNumber;
    private String fromPhoneNumber;
    private String documentStatus;
    private String documentStatusDate;
    private String receiveFaxCoverPageYn;
    private boolean hasAccountedTheFaxCoverPage = false;
    private String faxCoverSheetHtml;
    private String documentStatusHtmlFragment;
    private String username;
    private String password;
    private String endpoint;


    public ClientRequest build() {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setWsdlEndpoint(wsdlEndpoint);
        clientRequest.setWsdlUsername(wsdlUsername);
        clientRequest.setWsdlPassword(wsdlPassword);
        clientRequest.setCallbackEndpoint(callbackEndpoint);
        clientRequest.setCallbackUsername(callbackUsername);
        clientRequest.setCallbackPassword(callbackPassword);
        clientRequest.setFrom(from);
        clientRequest.setTo(to);
        clientRequest.setJobId(jobId);
        clientRequest.setSdateTime(sdateTime);
        clientRequest.setTimeout(timeout);
        clientRequest.setSchannel(schannel);
        clientRequest.setTransport(transport);
        clientRequest.setSubject(subject);
        clientRequest.setFileNumber(fileNumber);
        clientRequest.setSnumPages(snumPages);
        clientRequest.setAttachment(attachment);
        clientRequest.setExtension1(extension1);
        clientRequest.setExtension2(extension2);
        clientRequest.setFromFaxNumber(fromFaxNumber);
        clientRequest.setFromPhoneNumber(fromPhoneNumber);
        clientRequest.setDocumentStatus(documentStatus);
        clientRequest.setDocumentStatusDate(documentStatusDate);
        clientRequest.setReceiveFaxCoverPageYn(receiveFaxCoverPageYn);
        clientRequest.setFaxCoverSheetHtml(faxCoverSheetHtml);
        clientRequest.setDocumentStatusHtmlFragment(documentStatusHtmlFragment);
        clientRequest.setUsername(username);
        clientRequest.setPassword(password);
        clientRequest.setEndpoint(endpoint);

        validate(clientRequest);

        return clientRequest;
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
        this.snumPages += Integer.parseInt(snumPages);
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

        if (!hasAccountedTheFaxCoverPage && receiveFaxCoverPageYn != null && receiveFaxCoverPageYn.equalsIgnoreCase("Y")) {
            this.hasAccountedTheFaxCoverPage = true;
            this.snumPages += 1;
        }
        return this;
    }

    public ClientRequestBuilder setFaxCoverSheetHtml(String filepath) throws IOException, URISyntaxException {
        this.faxCoverSheetHtml = readFile(filepath);
        return this;
    }

    public ClientRequestBuilder setDocumentStatusHtmlFragment(String filepath) throws IOException, URISyntaxException {
        this.documentStatusHtmlFragment = readFile(filepath);
        return this;
    }

    public ClientRequestBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public ClientRequestBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public ClientRequestBuilder setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    private String readFile(String filePath) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(filePath);
        Stream<String> stream = Files.lines(Paths.get(resource.toURI()));
        return stream.collect(Collectors.joining("\n"));
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

    public String getFaxCoverSheetHtml() {
        return faxCoverSheetHtml;
    }

    public String getDocumentStatusHtmlFragment() {
        return documentStatusHtmlFragment;
    }

    public ClientRequestBuilder logInfo() {
        logger.info(toString());
        return this;
    }
}
