package ca.bc.gov.ag.efax.pdf.service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.bc.gov.ag.efax.pdf.util.PDFParser;
import ca.bc.gov.ag.outputservice.OutputServiceUtils;

@Service
public class PdfService {

    private static Logger logger = LoggerFactory.getLogger(PdfService.class);

    @Autowired
    private OutputServiceUtils outputServiceUtils;

    public File flattenPdf(String urlString, String fileName) {
        FileOutputStream fos = null;
        try {
            if (isFlattenable(urlString)) {
                byte[] bytes = outputServiceUtils.flattenPdfAsBytes(urlString);
                if (bytes != null) {
                    File file = new File(fileName);
                    
                    fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.flush();
                    
                    return file;
                }
            }
        } catch (Exception e) {
            logger.error("Error flattening PDF", e);
        } finally {
            if (fos != null)
                IOUtils.closeQuietly(fos);
        }
        return null;
    }

    /**
     * Returns <code>true</code> if the supplied url (that references a valid PDF) has a mime_type of "application/pdf", the pdf is parsable and has a
     * version > 1.5, <code>false</code> otherwise.
     * 
     * @param urlString
     * @return
     */
    private boolean isFlattenable(String urlString) {
        boolean flatten = false;
        PDFParser parser = new PDFParser(urlString);
        if (parser.pdfParsed() && parser.isPDFVersionGreaterThan(new BigDecimal("1.5"))) {
            flatten = true;
        }
        return flatten;
    }

}
