package ca.bc.gov.ag.proxy;

import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest.Attachments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ca.bc.gov.ag.proxy.validation.DocumentDistributionRequestBuilderValidator.validatePreBuild;

public class DocumentDistributionRequestBuilder {

    private static Logger logger = LoggerFactory.getLogger(DocumentDistributionRequestBuilder.class);

    private static final String RECIPIENT = "%RECIPIENT%";
    private static final String TOFAXNUMBER = "%TOFAXNUMBER%";
    private static final String SENDER = "%SENDER%";
    private static final String FROMFAXNUMBER = "%FROMFAXNUMBER%";
    private static final String FROMPHONENUMBER = "%FROMPHONENUMBER%";
    private static final String DATETIME = "%DATETIME%";
    private static final String SUBJECT = "%SUBJECT%";
    private static final String FILENUMBER = "%FILENUMBER%";
    private static final String NUMPAGES = "%NUMPAGES%";
    private static final String DOCUMENTSTATUSFRAGMENT = "%DOCUMENTSTATUSFRAGMENT%";
    private static final String DOCSTATUS = "%STATUS%";
    private static final String DOCSTATUSDATE = "%STATUSDATE%";

    private static final String FAX_COVER_SHEET_HTML_FILEPATH = "FaxCoverSheetHTMLTemplate.html";
    private static final String DOCUMENT_STATUS_HTML_FRAGMENT_FILEPATH = "DocumentStatusHTMLFragment.html";

    private String from;
    private String to;
    private String jobId;
    private Duration timeout;
    private String channel;
    private String transport;
    private String subject;
    private String body = "";
    private int numPages = 0; //not required
    private Attachments attachments = new Attachments();
    private String extension1 = "";
    private String extension2 = "";
    private boolean hasAccountedTheFaxCoverPage = false;

    // As seen in the logs, the stringDateTime value can have many formats so, we will use only for the fax page cover.
    // The actual request will use current system time(defined by the dateTime)
    private String stringDateTime;
    private XMLGregorianCalendar dateTime = getDateTime();
    private String fromFaxNumber;
    private String fromPhoneNumber;
    private String fileNumber;
    private String documentStatus = "";
    private String documentStatusDate = " ";

    private static XMLGregorianCalendar getDateTime() {
        Date current_date = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(current_date);
        XMLGregorianCalendar xmlGc = null;
        try {
            xmlGc = DatatypeFactory
                    .newInstance()
                    .newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return xmlGc;
    }

    public DocumentDistributionRequest build() throws IOException, URISyntaxException {
        validatePreBuild(this);

        constructBody();

        DocumentDistributionRequest documentDistributionRequest = new DocumentDistributionRequest();
        documentDistributionRequest.setFrom(from);
        documentDistributionRequest.setTo(to);
        documentDistributionRequest.setJobId(jobId);
        documentDistributionRequest.setDateTime(dateTime);
        documentDistributionRequest.setTimeout(timeout);
        documentDistributionRequest.setChannel(channel);
        documentDistributionRequest.setTransport(transport);
        documentDistributionRequest.setSubject(subject);
        documentDistributionRequest.setNumPages(numPages);
        documentDistributionRequest.setAttachments(attachments);
        documentDistributionRequest.setExtension1(extension1);
        documentDistributionRequest.setExtension2(extension2);
        documentDistributionRequest.setBody(body);

        System.out.println(documentDistributionRequest);
        return documentDistributionRequest;
    }

    private String getCoverSheet(String template) throws IOException, URISyntaxException {
        String docStatusFragment = "";
        if (!documentStatus.isEmpty()) {
            docStatusFragment = readFile(DOCUMENT_STATUS_HTML_FRAGMENT_FILEPATH);
            docStatusFragment = docStatusFragment.replaceAll(DOCSTATUS, documentStatus);
            docStatusFragment = docStatusFragment.replaceAll(DOCSTATUSDATE, documentStatusDate);
        }
        return docStatusFragment;
    }

    private void constructBody() throws IOException, URISyntaxException {
        String template = readFile(FAX_COVER_SHEET_HTML_FILEPATH);
        template = template.replaceAll(RECIPIENT, to);
        template = template.replaceAll(TOFAXNUMBER, transport);
        template = template.replaceAll(SENDER, from);
        template = template.replaceAll(FROMFAXNUMBER, fromFaxNumber);
        template = template.replaceAll(FROMPHONENUMBER, fromPhoneNumber);
        template = template.replaceAll(DATETIME, stringDateTime);
        template = template.replaceAll(SUBJECT, subject);
        template = template.replaceAll(FILENUMBER, fileNumber);
        template = template.replaceAll(NUMPAGES, Integer.toString(numPages));
        template = template.replaceAll(DOCUMENTSTATUSFRAGMENT, getCoverSheet(template));
        this.body = template;
    }

    public DocumentDistributionRequestBuilder setFrom(String from) {
        this.from = from;
        return this;
    }

    public DocumentDistributionRequestBuilder setTo(String to) {
        this.to = to;
        return this;
    }

    public DocumentDistributionRequestBuilder setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public DocumentDistributionRequestBuilder setStringDateTime(String stringDateTime) {
        this.stringDateTime = stringDateTime;
        return this;
    }

    public DocumentDistributionRequestBuilder setTimeout(String timeout) {
        try {
            this.timeout = DatatypeFactory
                    .newInstance()
                    .newDuration(timeout);

        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return this;
    }

    public DocumentDistributionRequestBuilder setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public DocumentDistributionRequestBuilder setTransport(String transport) {
        this.transport = transport;
        return this;
    }

    public DocumentDistributionRequestBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public DocumentDistributionRequestBuilder setNumPages(String numPages) {
        this.numPages = Integer.parseInt(numPages);

        if (hasAccountedTheFaxCoverPage)
            this.numPages += 1;

        return this;
    }

    public DocumentDistributionRequestBuilder setAttachments(String attachment) {
        if (attachment != null && !attachment.isEmpty())
            this.attachments.getUri().add(attachment);
        return this;
    }

    public DocumentDistributionRequestBuilder setExtension1(String extension1) {
        this.extension1 = extension1;
        return this;
    }

    public DocumentDistributionRequestBuilder setExtension2(String extension2) {
        this.extension2 = extension2;
        return this;
    }

    public DocumentDistributionRequestBuilder setFaxCoverPage(String receiveFaxCoverPageYn) {
        if (!hasAccountedTheFaxCoverPage
                && receiveFaxCoverPageYn != null
                && receiveFaxCoverPageYn.equalsIgnoreCase("Y")) {
            this.hasAccountedTheFaxCoverPage = true;
            this.numPages += 1;
        }
        return this;
    }

    public DocumentDistributionRequestBuilder setFromFaxNumber(String fromFaxNumber) {
        this.fromFaxNumber = fromFaxNumber;
        return this;
    }

    public DocumentDistributionRequestBuilder setFromPhoneNumber(String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
        return this;
    }

    private String readFile(String filePath) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(filePath);
        Stream<String> stream = Files.lines(Paths.get(resource.toURI()));
        return stream.collect(Collectors.joining("\n"));
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

    public Duration getTimeout() {
        return timeout;
    }

    public String getChannel() {
        return channel;
    }

    public String getTransport() {
        return transport;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public int getNumPages() {
        return numPages;
    }

    public Attachments getAttachments() {
        return attachments;
    }

    public String getExtension1() {
        return extension1;
    }

    public String getExtension2() {
        return extension2;
    }

    public String getStringDateTime() {
        return stringDateTime;
    }

    public String getFromFaxNumber() {
        return fromFaxNumber;
    }

    public String getFromPhoneNumber() {
        return fromPhoneNumber;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public DocumentDistributionRequestBuilder setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
        return this;
    }

    public DocumentDistributionRequestBuilder setDocumentStatus(String documentStatus) {
        if (documentStatus != null)
            this.documentStatus = documentStatus;
        return this;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public String getDocumentStatusDate() {
        return documentStatusDate;
    }

    public DocumentDistributionRequestBuilder setDocumentStatusDate(String documentStatusDate) {
        if (documentStatus != null && !documentStatusDate.isEmpty())
            this.documentStatusDate = documentStatusDate;
        return this;
    }
}
