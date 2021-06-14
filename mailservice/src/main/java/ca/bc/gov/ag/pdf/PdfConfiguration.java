package ca.bc.gov.ag.pdf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.bc.gov.ag.outputservice.OutputServiceUtils;

@Configuration
@EnableConfigurationProperties(PdfProperties.class)
public class PdfConfiguration {

	@Bean
	public OutputServiceUtils outputServiceUtils(PdfProperties pdfProperties) {
		OutputServiceUtils outputServiceUtils = new OutputServiceUtils(
				pdfProperties.getEndpoint(), 
				pdfProperties.getUsername(), 
				pdfProperties.getPassword());
		return outputServiceUtils;
	}
	
}
