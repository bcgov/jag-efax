package ca.bc.gov.ag.proxy.service;

import ca.bc.gov.ag.efax.ws.config.WebServiceConfig;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.StringWriter;

public class DocumentDistributionService {
    private final String soapEndpointUrl;
    private final String soapAction;
    private final DocumentDistributionRequest documentDistributionRequest;
    private final SOAPMessage soapMessage;
    private final SOAPEnvelope soapEnvelope;

    private final String documentDistributionRequestNamespace = "DocumentDistributionRequest";

    public DocumentDistributionService(String soapEndpointUrl, String soapAction, DocumentDistributionRequest documentDistributionRequest) throws SOAPException {
        this.soapEndpointUrl = soapEndpointUrl;
        this.soapAction = soapAction;
        this.documentDistributionRequest = documentDistributionRequest;

        this.soapMessage = MessageFactory.newInstance().createMessage();
        this.soapEnvelope = createSoapEnvelope();
    }

    private SOAPEnvelope createSoapEnvelope() throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addAttribute(new QName("targetNamespace"), WebServiceConfig.NAMESPACE_URI);
        envelope.addNamespaceDeclaration("", "http://www.w3.org/2001/XMLSchema");

        return createBody(envelope);
    }

    private SOAPEnvelope createBody(SOAPEnvelope envelope) throws SOAPException {
        SOAPBody soapBody = envelope.getBody();
        String x;
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(DocumentDistributionRequest.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

            jaxbMarshaller.marshal(documentDistributionRequest, sw);
            x = sw.toString();
            soapBody.addTextNode(x);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return envelope;
    }

    public String callSoapWebService(String username, String password) {
        String response = "";
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(username, password), soapEndpointUrl);

            // Print the SOAP Response
            response = soapResponse.toString();
            soapResponse.writeTo(System.out);

            soapConnection.close();
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
        return response;
    }

    private SOAPMessage createSOAPRequest(String username, String password) throws Exception {
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        String userAndPassword = String.format("%s:%s", username, password);
        String basicAuth = new sun.misc.BASE64Encoder().encode(userAndPassword.getBytes());
        headers.addHeader("Authorization", "Basic " + basicAuth);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }
}
