package ca.bc.gov.ag.efax.pdf.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.outputservice.OutputServiceUtils;

public class PdfServiceTest extends BaseTestSuite {

    @Mock
    private OutputServiceUtils outputServiceUtils;
    
    @InjectMocks
    private PdfService pdfService;

    @BeforeEach
    @Override
    protected void beforeEach() throws Exception {
        super.beforeEach();

        when(outputServiceUtils.flattenPdfAsBytes(anyString())).thenReturn(new byte[0]);
    }
    
    @Test
    void testFlattenPdf_1_4() throws Exception {
        // A v1.4 version PDF should not be flattened
        URL url = getClass().getClassLoader().getResource("sample_v1.4.pdf");
        File file = pdfService.flattenPdf(url.toString(), "sample_v1.4_flat.pdf");
        assertNull(file);
    }
    
    @Test
    void testFlattenPdf_1_6() throws Exception {
        // A v1.6 version PDF can be flattened
        URL url = getClass().getClassLoader().getResource("sample_v1.6.pdf");
        File file = pdfService.flattenPdf(url.toString(), "sample_v1.6_flat.pdf");
        assertNotNull(file);
    }
}
