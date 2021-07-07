package ca.bc.gov.ag.efax.pdf.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.adobe.pdf.PDFDocument;
import com.adobe.pdf.PDFFactory;


public class PDFParser {
    private final static String MIME_TYPE = "application/pdf";
    private PDFDocument pdf;
    private boolean parsed = false;
    
    public PDFParser(String urlString) {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            // TODO: could MIME_TYPE also be "application/octet-stream"?
            if (MIME_TYPE.equalsIgnoreCase(conn.getContentType())) {
                inputStream = conn.getInputStream();
                pdf = PDFFactory.openDocument(inputStream);
                parsed = true;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
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
