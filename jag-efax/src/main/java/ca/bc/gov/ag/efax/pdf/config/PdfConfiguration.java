package ca.bc.gov.ag.efax.pdf.config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.soap.SOAPBinding;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import ca.bc.gov.ag.efax.pdf.outputservice.model.TransformPDF;
import ca.bc.gov.ag.efax.pdf.outputservice.model.TransformPDFResponse;

@Configuration
@EnableConfigurationProperties(PdfProperties.class)
public class PdfConfiguration {

    @Bean
    public Jaxb2Marshaller pdfMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in pom.xml
        marshaller.setContextPath("ca.bc.gov.ag.efax.pdf.livecycle.model");
        return marshaller;
    }

    @Bean
    public Dispatch<Object> outputServiceDispatch(PdfProperties pdfProperties) throws JAXBException {
        QName serviceName = new QName("http://adobe.com/idp/services", "OutputServiceService");
        QName portQName = new QName("http://adobe.com/idp/services", "OutputService");
        String endpoint = pdfProperties.getEndpoint();

        javax.xml.ws.Service service = javax.xml.ws.Service.create(serviceName);
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING, endpoint);

        JAXBContext jc = JAXBContext.newInstance(TransformPDF.class, TransformPDFResponse.class);
        Dispatch<Object> pdfDispatch = service.createDispatch(portQName, jc, javax.xml.ws.Service.Mode.PAYLOAD);

        BindingProvider prov = (BindingProvider) pdfDispatch;
        prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, pdfProperties.getUsername());
        prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pdfProperties.getPassword());

        return pdfDispatch;
    }

}
