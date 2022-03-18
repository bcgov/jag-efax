package ca.bc.gov.ag.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

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

    private static Logger logger = LoggerFactory.getLogger(DistributionClient.class);

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
        try {
            ClientRequest clientRequest = builder
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
                    .setReceiveFaxCoverPageYn(receiveFaxCoverPageYn)
                    .setFaxCoverSheetHtml("DocumentStatusHTMLFragment.html")
                    .setDocumentStatusHtmlFragment("FaxCoverSheetHTMLTemplate.html")
                    .setEndpoint(wsdlEndpoint)
                    .setUsername(wsdlUsername)
                    .setPassword(wsdlUsername)
                    .logInfo()
                    .build();

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
        logger.info(builder.getDocumentStatusHtmlFragment());
        return null;
    }

    public static void main(String[] args) {
        String wsdlEndpoint = "http://nginx-ddea46-test.apps.silver.devops.gov.bc.ca/api/ws";
        String wsdlUsername = "icedtest";
        String wsdlPassword = "***REMOVED***";
        String callbackEndpoint = "http://wsgw.test.jag.gov.bc.ca:8080/efax/JustinDistributionCallback";
        String callbackUsername = "callbackUsername";
        String callbackPassword = "callbackPassword";

        String from = "Steven Dickson";
        String to = "Joe Bloggs";
        String jobId = "86420";
        String sdateTime = "2008-12-12T12:12:12.121-0800";
        String timeout = "PT3M";
        String schannel = "fax";
        String transport = "250 356 9293";
        String subject = "Distribution Client - Testing";
        String fileNumber = "F12345-0123";
//        String snumPages = "3";
        String snumPages = "0";
        String attachment = "https://eservice.ag.gov.bc.ca/cso/about/efiling-info.pdf";
        String extension1 = "";
        String extension2 = "";
        String fromFaxNumber = "(250) 356 9293";
        String fromPhoneNumber = "(250) 590 2252";
        String documentStatus = "Cancelled";
        String documentStatusDate = "2008-06-26";

        String receiveFaxCoverPageYn = "Y";


        process(wsdlEndpoint,
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
    }


}
