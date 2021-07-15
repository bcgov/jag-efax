package ca.bc.gov.ag.efax.pdf.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.net.URL;

import org.junit.jupiter.api.Test;

public class PDFParserTest {

    @Test
    void testIsPDFVersionGreaterThan_1_4() throws Exception {
        URL url = getClass().getClassLoader().getResource("sample_v1.4.pdf");
        
        PDFParser parser = new PDFParser(url.toString());
        assertTrue(parser.pdfParsed());
        assertEquals("%PDF-1.4", parser.getVersion());
        assertTrue(parser.isPDFVersionGreaterThan(BigDecimal.valueOf(1.3)));
        assertFalse(parser.isPDFVersionGreaterThan(BigDecimal.valueOf(1.4)));        
    }

    @Test
    void testIsPDFVersionGreaterThan_1_6() throws Exception {
        URL url = getClass().getClassLoader().getResource("sample_v1.6.pdf");
        
        PDFParser parser = new PDFParser(url.toString());
        assertTrue(parser.pdfParsed());
        assertEquals("%PDF-1.6", parser.getVersion());
        assertTrue(parser.isPDFVersionGreaterThan(BigDecimal.valueOf(1.5)));
        assertFalse(parser.isPDFVersionGreaterThan(BigDecimal.valueOf(1.6))); 
        assertFalse(parser.isPDFVersionGreaterThan(null));        
    }

    @Test
    void testIsPDFVersionGreaterThan_none() throws Exception {        
        PDFParser parser = new PDFParser("invalid url");
        assertFalse(parser.pdfParsed());    
    }
}
