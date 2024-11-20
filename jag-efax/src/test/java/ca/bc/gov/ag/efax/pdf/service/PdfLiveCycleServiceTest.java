package ca.bc.gov.ag.efax.pdf.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.net.URL;

import ca.bc.gov.ag.efax.graph.config.MSGraphProperties;
import ca.bc.gov.ag.efax.redis.TestRedisConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.client.core.WebServiceTemplate;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.pdf.config.PdfProperties;
import ca.bc.gov.ag.efax.pdf.livecycle.model.ObjectFactory;
import ca.bc.gov.ag.efax.pdf.livecycle.model.PDFTransformationsResponse;
import ca.bc.gov.ag.efax.pdf.util.PdfUtils;

public class PdfLiveCycleServiceTest extends BaseTestSuite {

    private PdfLiveCycleService pdfService;

    @Autowired
    private MSGraphProperties graphProperties;

    @Autowired
    private Jaxb2Marshaller pdfMarshaller;

    @Autowired
    private PdfProperties pdfProperties;

    @BeforeEach
    protected void beforeEach() throws Exception {
        pdfService = new PdfLiveCycleService(pdfMarshaller, pdfProperties);
    }

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
        String filePath = graphProperties.getTempDirectory() + "sample_v1.6.flat.pdf";
        File file = pdfService.flattenPdf(url, filePath, "1234");

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
        String filePath = graphProperties.getTempDirectory() + "sample_v1.6.flat.pdf";
        File file = pdfService.flattenPdf(url, filePath, "1234");

        // On exception, quietly catch exception and return null
        assertNull(file);
    }
}
