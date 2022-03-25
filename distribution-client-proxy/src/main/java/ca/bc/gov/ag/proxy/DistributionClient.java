package ca.bc.gov.ag.proxy;

import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.proxy.service.DocumentDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.StringJoiner;

public class DistributionClient {

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
            final String documentStatusDate
    ) {
        return process(wsdlEndpoint,
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
                "Y");
    }

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

        logArguments(wsdlEndpoint,
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

        String response = "";
        try {
            DocumentDistributionRequest clientRequest = new DocumentDistributionRequestBuilder()
                    .setFrom(from)
                    .setTo(to)
                    .setJobId(jobId)
                    .setStringDateTime(sdateTime)
                    .setTimeout(timeout)
                    .setChannel(schannel)
                    .setTransport(transport)
                    .setSubject(subject)
                    .setFaxCoverPage(receiveFaxCoverPageYn)
                    .setNumPages(snumPages)
                    .setAttachments(attachment)
                    .setExtension1(extension1)
                    .setExtension2(extension2)
                    .setFromFaxNumber(fromFaxNumber)
                    .setFromPhoneNumber(fromPhoneNumber)
                    .setFileNumber(fileNumber)
                    .setDocumentStatus(documentStatus)
                    .setDocumentStatusDate(documentStatusDate)
                    .build();
            DocumentDistributionService distributionService = new DocumentDistributionService(
                    wsdlEndpoint,
                    "initiate",
                    clientRequest);
            response = distributionService.callSoapWebService(wsdlUsername, wsdlPassword);
        } catch (IOException | URISyntaxException | SOAPException e) {
            e.printStackTrace();
        }

        return response;

    }

    private static void logArguments(String wsdlEndpoint, String wsdlUsername, String wsdlPassword, String callbackEndpoint, String callbackUsername, String callbackPassword, String from, String to, String jobId, String sdateTime, String timeout, String schannel, String transport, String subject, String fileNumber, String snumPages, String attachment, String extension1, String extension2, String fromFaxNumber, String fromPhoneNumber, String documentStatus, String documentStatusDate, String receiveFaxCoverPageYn) {
        final String nlChar = System.getProperty("line.separator");

        String msg = new StringJoiner(nlChar)
                .add("client.process{")
                .add("wsdlEndpoint: " + wsdlEndpoint)
                .add("wsdlUsername: " + wsdlUsername)
                .add("wsdlPassword: " + wsdlPassword)
                .add("callbackEndpoint: " + callbackEndpoint)
                .add("callbackUsername: " + callbackUsername)
                .add("callbackPassword: " + callbackPassword)
                .add("from: " + from)
                .add("to: " + to)
                .add("jobId: " + jobId)
                .add("sdateTime: " + sdateTime)
                .add("timeout: " + timeout)
                .add("schannel: " + schannel)
                .add("transport: " + transport)
                .add("subject: " + subject)
                .add("fileNumber: " + fileNumber)
                .add("snumPages: " + snumPages)
                .add("attachment: " + attachment)
                .add("extension1: " + extension1)
                .add("extension2: " + extension2)
                .add("fromFaxNumber: " + fromFaxNumber)
                .add("fromPhoneNumber: " + fromPhoneNumber)
                .add("documentStatus: " + documentStatus)
                .add("documentStatusDate: " + documentStatusDate)
                .add("receiveFaxCoverPageYn: " + receiveFaxCoverPageYn)
                .add("}")
                .toString();

        logger.info(msg);
    }
}
