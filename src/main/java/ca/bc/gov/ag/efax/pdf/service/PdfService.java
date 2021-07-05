package ca.bc.gov.ag.efax.pdf.service;

import java.io.OutputStream;
import java.math.BigDecimal;

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

	public boolean flattenPdf(String urlString, OutputStream outputStream) {
		try {
			if (isFlattenable(urlString)) {
				byte[] bytes = outputServiceUtils.flattenPdfAsBytes(urlString);
				if (bytes != null) {
					outputStream.write(bytes);
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("Error flattening PDF", e);
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if the supplied url (that references a valid PDF)
	 * has a mime_type of "application/pdf", the pdf is parsable and has a version >
	 * 1.5, <code>false</code> otherwise.
	 * 
	 * @param urlStr
	 * @return
	 */
	private boolean isFlattenable(String urlStr) {
		boolean flatten = false;
		PDFParser parser = new PDFParser(urlStr);
		if (parser.pdfParsed() && parser.isPDFVersionGreaterThan(new BigDecimal("1.5"))) {
			flatten = true;
		}
		return flatten;
	}

}
