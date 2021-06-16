package ca.bc.gov.ag.pdf;

import com.adobe.pdf.PDFDocument;
import com.adobe.pdf.PDFFactory;

import java.math.BigDecimal;

import java.net.URL;
import java.net.URLConnection;


public class PDFParser {
    private final static String MIME_TYPE = "application/pdf";
    private PDFDocument pdf;
    private boolean parsed = false;
    
    public PDFParser(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            if (MIME_TYPE.equalsIgnoreCase(conn.getContentType())) {
                pdf = PDFFactory.openDocument(conn.getInputStream());
                parsed = true;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public boolean pdfParsed() {
        return parsed;
    }
    
    public String getVersion() {
        return parsed ? pdf.getVersion() : "";
    }
    
    public boolean isPDFVersionGreaterThan(BigDecimal version) {
        if (parsed && (version != null)) {
            String pdfVersion = pdf.getVersion();
            pdfVersion = pdfVersion.substring(pdfVersion.lastIndexOf("-") + 1);
            return version.compareTo(new BigDecimal(pdfVersion)) < 0;
        }
        return false;
    }
}
