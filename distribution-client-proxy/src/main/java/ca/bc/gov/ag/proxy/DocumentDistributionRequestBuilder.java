package ca.bc.gov.ag.proxy;

import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest.Attachments;
import ca.bc.gov.ag.efax.ws.model.ObjectFactory;
import ca.bc.gov.ag.proxy.config.ApplicationProperties;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringJoiner;

import static ca.bc.gov.ag.proxy.validation.DocumentDistributionRequestBuilderValidator.validatePreBuild;

public class DocumentDistributionRequestBuilder {

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

    private String from;
    private String to;
    private String jobId;
    private Duration timeout;
    private String channel;
    private String transport;
    private String subject;
    private String body = "";
    private int numPages = 0; //not required
    private final Attachments attachments = new Attachments();
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

        DocumentDistributionRequest documentDistributionRequest = new ObjectFactory().createDocumentDistributionRequest();
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

        return documentDistributionRequest;
    }

    private String getCoverSheet(String template) throws IOException, URISyntaxException {
        String docStatusFragment = "";
        if (!documentStatus.isEmpty()) {
            docStatusFragment = readFile(ApplicationProperties.getDocumentStatusHtmlFragmentFilepath());
            docStatusFragment = docStatusFragment.replaceAll(DOCSTATUS, documentStatus);
            docStatusFragment = docStatusFragment.replaceAll(DOCSTATUSDATE, documentStatusDate);
        }
        return docStatusFragment;
    }

    private void constructBody() throws IOException, URISyntaxException {
        String template = readFile(ApplicationProperties.getFaxCoverSheetHtmlFilepath());
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

        template = StringEscapeUtils.unescapeHtml4(template);
        template = StringEscapeUtils.unescapeJava(template);

        this.body = cdataWrap(template);
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
        String s = StringEscapeUtils.unescapeHtml4(subject);
        s = StringEscapeUtils.unescapeJava(s);
        s = s.replaceAll("\u00A0", " ");
        s = s.replaceAll("\\<.*?\\>", "");
        this.subject = s;
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

    private String readFile(String filePath) throws IllegalArgumentException {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(filePath);
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        }

        StringJoiner fileContent = new StringJoiner("\n");
        try (InputStreamReader streamReader = new InputStreamReader(resource, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
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
        if (documentStatusDate != null && !documentStatusDate.isEmpty())
            this.documentStatusDate = documentStatusDate;
        return this;
    }

    private String cdataWrap(final String text){
        return "<![CDATA[" + text + "]]>";
    }
}
