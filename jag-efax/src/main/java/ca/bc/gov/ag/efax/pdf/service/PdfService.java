package ca.bc.gov.ag.efax.pdf.service;

import java.io.File;
import java.net.URL;

public interface PdfService {

    /**
     * Attempts to flatten (aka normalize/simplify) a PDF by delegating the call to a web service.
     * @param url a URL to a PDF to flatten
     * @param path the full fileName for the name of file to return.
     * @param jobId for logging purposes, the jobId for this transaction.
     * @return a flattened PDF, or null if an error occurred.
     */
    public File flattenPdf(URL url, String path, String jobId);
    
}
