package ca.bc.gov.ag.efax.pdf.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.ws.Dispatch;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import ca.bc.gov.ag.efax.pdf.outputservice.model.BLOB;
import ca.bc.gov.ag.efax.pdf.outputservice.model.TransformPDF;
import ca.bc.gov.ag.efax.pdf.outputservice.model.TransformPDFResponse;

@Service
@ConditionalOnProperty(
        name = "aem.output.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class PdfOutputService extends WebServiceGatewaySupport implements PdfService {
        
    private Logger logger = LoggerFactory.getLogger(PdfOutputService.class);

    private final WebServiceTemplate webServiceTemplate;
    @Value("${aem.output.endpoint}")
    private String host;

    public PdfOutputService(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    /**
     * Attempts to flatten (aka normalize/simplify) a PDF by delegating the call to a web service.
     * @param url a URL to a PDF to flatten
     * @param path the full fileName for the name of file to return.
     * @param jobId for logging purposes, the jobId for this transaction.
     * @return a flattened PDF, or null if an error occurred.
     */
    public File flattenPdf(URL url, String path, String jobId) {
    	
        try {
            logger.info("PDF OutputService Flattening: jobId {} attempting flattening.", jobId);

            BLOB inPdfDoc = new BLOB();
            inPdfDoc.setRemoteURL(url.toString());
            TransformPDF request = new TransformPDF();
            request.setInPdfDoc(inPdfDoc);
            TransformPDFResponse response=
                    (TransformPDFResponse) webServiceTemplate.marshalSendAndReceive(host, request);
            
            logger.info("PDF OutputService Flattening: jobId {} flattening successful.", jobId);

            return extractResults(response, path);
        } catch (Exception e) {
            logger.error("Error calling OutputService's TransformPDF service", e);
            return null;
        }
    }

    private File extractResults(TransformPDFResponse response, String path) throws IOException {
        if (response != null && response.getOutPdfDoc() != null) {
            File file = new File(path);
            FileUtils.writeByteArrayToFile(file, response.getOutPdfDoc().getBinaryData());
            return file;
        }
        return null;
    }

}
