package ca.bc.gov.ag.efax.pdf.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.mail.config.ExchangeProperties;
import ca.bc.gov.ag.efax.pdf.model.ObjectFactory;
import ca.bc.gov.ag.efax.pdf.model.PDFTransformationsResponse;
import ca.bc.gov.ag.efax.pdf.util.PdfUtils;

public class PdfServiceTest extends BaseTestSuite {

    @Autowired
    protected PdfService pdfService;

    @Autowired
    private ExchangeProperties exchangeProperties;
    
    @Test
    void testFlattenPdf() throws Exception {
        URL url = getClass().getClassLoader().getResource("sample_v1.6.pdf");
        
        // mock webServiceTemplate to return the same data queried.
        WebServiceTemplate webServiceTemplate = Mockito.mock(WebServiceTemplate.class);
        pdfService.setWebServiceTemplate(webServiceTemplate);
        PDFTransformationsResponse value = new PDFTransformationsResponse();
        value.setPDFTransformationsReturn(PdfUtils.readUrl(url));
        Mockito.when(webServiceTemplate.marshalSendAndReceive(Mockito.any())).thenReturn(new ObjectFactory().createPDFTransformationsResponse(value));

        // Attempt to flatten a PDF doc.
        String filePath = exchangeProperties.getTempDirectory() + "sample_v1.6.flat.pdf";
        File file = pdfService.flattenPdf(url, filePath);
        
        assertNotNull(file);
        
        FileUtils.deleteQuietly(file);
    }
    
    @Test
    void testFlattenPdf_thrownError() throws Exception {
        URL url = getClass().getClassLoader().getResource("sample_v1.6.pdf");
        
        // mock webServiceTemplate to return the same data queried.
        WebServiceTemplate webServiceTemplate = Mockito.mock(WebServiceTemplate.class);
        pdfService.setWebServiceTemplate(webServiceTemplate);
        PDFTransformationsResponse value = new PDFTransformationsResponse();
        value.setPDFTransformationsReturn(PdfUtils.readUrl(url));
        Mockito.when(webServiceTemplate.marshalSendAndReceive(Mockito.any())).thenThrow(new RuntimeException("something happened."));

        // Attempt to flatten a PDF doc.
        String filePath = exchangeProperties.getTempDirectory() + "sample_v1.6.flat.pdf";
        File file = pdfService.flattenPdf(url, filePath);
        
        // On exception, quietly catch exception and return null
        assertNull(file);
    }
    
}
