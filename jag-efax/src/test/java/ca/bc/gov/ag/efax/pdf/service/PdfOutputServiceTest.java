package ca.bc.gov.ag.efax.pdf.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.mail.config.ExchangeProperties;
import ca.bc.gov.ag.efax.pdf.outputservice.model.BLOB;
import ca.bc.gov.ag.efax.pdf.outputservice.model.TransformPDFResponse;

public class PdfOutputServiceTest extends BaseTestSuite {

    @Autowired
    private PdfService pdfService;
    
    @Autowired
    private ExchangeProperties exchangeProperties;
        
    @Test
    void testFlattenPdf() throws Exception {
        URL url = getClass().getClassLoader().getResource("sample_v1.6.pdf");

        TransformPDFResponse response = new TransformPDFResponse();
        BLOB responseBlob = new BLOB();
        responseBlob.setBinaryData(new byte[0]);
        response.setOutPdfDoc(responseBlob);
        Mockito.when(outputServiceDispatch.invoke(Mockito.any())).thenReturn(response);

        // Attempt to flatten a PDF doc.
        String filePath = exchangeProperties.getTempDirectory() + "sample_v1.6.flat.pdf";
        File file = pdfService.flattenPdf(url, filePath, "1234");
        
        assertNotNull(file);
        
        FileUtils.deleteQuietly(file);
    }
    
    @Test
    void testFlattenPdf_thrownError() throws Exception {
        URL url = getClass().getClassLoader().getResource("sample_v1.6.pdf");
        
        // mock webServiceTemplate to return the same data queried.
        Mockito.when(outputServiceDispatch.invoke(Mockito.any())).thenThrow(new RuntimeException("something happened."));

        // Attempt to flatten a PDF doc.
        String filePath = exchangeProperties.getTempDirectory() + "sample_v1.6.flat.pdf";
        File file = pdfService.flattenPdf(url, filePath, "1234");
        
        // On exception, quietly catch exception and return null
        assertNull(file);
    }
    
}
