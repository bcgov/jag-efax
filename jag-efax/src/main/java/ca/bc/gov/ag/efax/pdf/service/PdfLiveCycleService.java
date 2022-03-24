package ca.bc.gov.ag.efax.pdf.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

import javax.xml.bind.JAXBElement;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import ca.bc.gov.ag.efax.pdf.config.PdfProperties;
import ca.bc.gov.ag.efax.pdf.livecycle.model.ObjectFactory;
import ca.bc.gov.ag.efax.pdf.livecycle.model.PDFTransformations;
import ca.bc.gov.ag.efax.pdf.livecycle.model.PDFTransformationsResponse;
import ca.bc.gov.ag.efax.pdf.util.PdfUtils;

@Service
@ConditionalOnProperty(
        name = "aem.livecycle.enabled",
        havingValue = "true",
        matchIfMissing = false)
public class PdfLiveCycleService extends WebServiceGatewaySupport implements PdfService {
        
    private Logger logger = LoggerFactory.getLogger(PdfLiveCycleService.class);

    public PdfLiveCycleService(Jaxb2Marshaller pdfMarshaller, PdfProperties pdfProperties) {        
        setDefaultUri(pdfProperties.getEndpoint());
        setMarshaller(pdfMarshaller);
        setUnmarshaller(pdfMarshaller);
    }

    /**
     * Attempts to flatten (aka normalize/simplify) a PDF by delegating the call to a web service.
     * @param url a URL to a PDF to flatten
     * @param path the full fileName for the name of file to return.
     * @param jobId for logging purposes, the jobId for this transaction.
     * @return a flattened PDF, or null if an error occurred.
     */
    @SuppressWarnings("unchecked")
    public File flattenPdf(URL url, String path, String jobId) {
        try {
            byte[] data = PdfUtils.readUrl(url);
            if (data != null) {
                logger.info("PDF Flattening: jobId {} attempting flattening.", jobId);
                
                // convert a url to a pdf file to that of a base64 encoded string.
                String encoded = Base64.getEncoder().encodeToString(data);
    
                PDFTransformations request = new PDFTransformations();
                request.setFlags(96);
                request.setInputFile(encoded);
                
                JAXBElement<PDFTransformationsResponse> jaxbResponse = (JAXBElement<PDFTransformationsResponse>) getWebServiceTemplate()
                        .marshalSendAndReceive(new ObjectFactory().createPDFTransformations(request));

                logger.info("PDF Flattening: jobId {} flattening successful.", jobId);
                
                return extractResults(jaxbResponse, path);
            }
        }
        catch (Exception e) {
            logger.error("Error calling LiveCycleGateway's PDFTransformations service", e);
        }

        return null;
    }
    
    private File extractResults(JAXBElement<PDFTransformationsResponse> jaxbResponse, String path) throws IOException {
        if (jaxbResponse != null) {
            PDFTransformationsResponse response = jaxbResponse.getValue();
            if (response != null && response.getPDFTransformationsReturn() != null) {
                File file = new File(path);
                FileUtils.writeByteArrayToFile(file, response.getPDFTransformationsReturn());
                return file;
            }
        }  
        return null;
    }

}
