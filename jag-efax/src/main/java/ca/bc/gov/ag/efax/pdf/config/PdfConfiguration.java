package ca.bc.gov.ag.efax.pdf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import jakarta.xml.soap.SOAPMessage;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(PdfProperties.class)
public class PdfConfiguration {

    @Autowired
    private WebServiceSenderWithAuth webServiceSenderWithAuth;

    @Bean
    public Jaxb2Marshaller pdfMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in pom.xml
        marshaller.setContextPath("ca.bc.gov.ag.efax.pdf.livecycle.model");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        webServiceTemplate.setMessageFactory(messageFactory());
        webServiceTemplate.setMessageSender(webServiceSenderWithAuth);
        jaxb2Marshaller.setContextPaths(
                "ca.bc.gov.ag.efax.pdf.outputservice.model");
        webServiceTemplate.setMarshaller(jaxb2Marshaller);
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller);
        webServiceTemplate.afterPropertiesSet();
        return webServiceTemplate;
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(SOAPMessage.WRITE_XML_DECLARATION, "true");
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setMessageProperties(props);
        messageFactory.setSoapVersion(SoapVersion.SOAP_11);
        return messageFactory;
    }

}
