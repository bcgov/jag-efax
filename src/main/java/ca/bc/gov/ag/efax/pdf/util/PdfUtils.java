package ca.bc.gov.ag.efax.pdf.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfUtils {

    private final static String MIME_TYPE = "application/pdf";
    
    private static Logger logger = LoggerFactory.getLogger(PdfUtils.class);
    
    /**
     * Reads and converts a URL to byte[] iff the contentType of the URL is "application/pdf".
     */
    public static byte[] readUrl(URL url) throws IOException {
        InputStream inputStream = null;
        try {
            URLConnection conn = url.openConnection();
            String contentType = conn.getContentType();
            if (contentType != null && contentType.contains(MIME_TYPE)) {
                inputStream = conn.getInputStream();
                return IOUtils.toByteArray(inputStream);
            }
            else {
                logger.info("Mimetype of PDF URL is not 'application/pdf', but rather '{}'. Not flattening doc.", contentType);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
        return null;
    }
    
}
