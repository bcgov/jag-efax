package ca.bc.gov.ag.proxy;

import ca.bc.gov.ag.efax.pdf.util.PdfUtils;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.proxy.validation.ClientRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class DistributionClient {

    private static final String nlChar = System.getProperty("line.separator");
    private static final String filesepChar = System.getProperty("file.separator");
    private static final String sepChar = System.getProperty("path.separator");

    // Read from properties file
    private String faxCoverSheetHtml = "";
    private String documentStatusHtmlFragment = "";

    // Placeholder tokens
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

    private static Logger logger = LoggerFactory.getLogger(PdfUtils.class);

    public static String process(
            final String wsdlEndpoint,
            final String wsdlUsername,
            final String wsdlPassword,
            final String callbackEndpoint,
            final String callbackUsername,
            final String callbackPassword,
            final String from,
            final String to,
            final String jobId,
            final String sdateTime,
            final String timeout,
            final String schannel,
            final String transport,
            final String subject,
            final String fileNumber,
            final String snumPages,
            final String attachment,
            final String extension1,
            final String extension2,
            final String fromFaxNumber,
            final String fromPhoneNumber,
            final String documentStatus,
            final String documentStatusDate,
            final String receiveFaxCoverPageYn
    ) {

        ClientRequestBuilder builder = new ClientRequestBuilder();
        builder
                .setWsdlEndpoint(wsdlEndpoint)
                .setWsdlUsername(wsdlUsername)
                .setWsdlPassword(wsdlPassword)
                .setCallbackEndpoint(callbackEndpoint)
                .setCallbackUsername(callbackUsername)
                .setCallbackPassword(callbackPassword)
                .setFrom(from)
                .setTo(to)
                .setJobId(jobId)
                .setSdateTime(sdateTime)
                .setTimeout(timeout)
                .setSchannel(schannel)
                .setTransport(transport)
                .setSubject(subject)
                .setFileNumber(fileNumber)
                .setSnumPages(snumPages)
                .setAttachment(attachment)
                .setExtension1(extension1)
                .setExtension2(extension2)
                .setFromFaxNumber(fromFaxNumber)
                .setFromPhoneNumber(fromPhoneNumber)
                .setDocumentStatus(documentStatus)
                .setDocumentStatusDate(documentStatusDate)
                .setReceiveFaxCoverPageYn(receiveFaxCoverPageYn);

        logger.info(builder.toString());

        ClientRequest clientRequest = builder.build();

        return null;
    }

    public static void main(String[] args) {
        String wsdlEndpoint="wsdlEndpoint";
        String wsdlUsername="wsdlUsername";
        String wsdlPassword="wsdlPassword";
        String callbackEndpoint="callbackEndpoint";
        String callbackUsername="callbackUsername";
        String callbackPassword="callbackPassword";
        String from="from";
        String to="to";
        String jobId="jobId";
        String sdateTime="sdateTime";
        String timeout="timeout";
        String schannel="schannel";
        String transport="transport";
        String subject="subject";
        String fileNumber="fileNumber";
        String snumPages="snumPages";
        String attachment="attachment";
        String extension1="extension1";
        String extension2="extension2";
        String fromFaxNumber="fromFaxNumber";
        String fromPhoneNumber="fromPhoneNumber";
        String documentStatus="documentStatus";
        String documentStatusDate="documentStatusDate";
        String receiveFaxCoverPageYn="receiveFaxCoverPageYn";
        ClientRequest clientRequest = new ClientRequest(wsdlEndpoint,
                wsdlUsername,
                wsdlPassword,
                callbackEndpoint,
                callbackUsername,
                callbackPassword,
                from,
                to,
                jobId,
                sdateTime,
                timeout,
                schannel,
                transport,
                subject,
                fileNumber,
                snumPages,
                attachment,
                extension1,
                extension2,
                fromFaxNumber,
                fromPhoneNumber,
                documentStatus,
                documentStatusDate,
                receiveFaxCoverPageYn);

        ClientRequestValidator.validate(clientRequest);


    }


}
