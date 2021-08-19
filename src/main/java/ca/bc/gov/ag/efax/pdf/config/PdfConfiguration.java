package ca.bc.gov.ag.efax.pdf.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@EnableConfigurationProperties(PdfProperties.class)
public class PdfConfiguration {

    @Bean
    public Jaxb2Marshaller pdfMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in pom.xml
        marshaller.setContextPath("ca.bc.gov.ag.efax.pdf.model");
        return marshaller;
    }

}
