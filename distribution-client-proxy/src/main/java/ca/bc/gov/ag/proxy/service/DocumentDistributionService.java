package ca.bc.gov.ag.proxy.service;

import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Base64;

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
        envelope.addNamespaceDeclaration("ns2", "http://ag.gov.bc.ca/DocumentDistributionMainProcess");

        SOAPEnvelope body = createBody(envelope);
        return body;
    }

    private SOAPEnvelope createBody(SOAPEnvelope envelope) throws SOAPException {
        SOAPBody soapBody = envelope.getBody();
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(DocumentDistributionRequest.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(documentDistributionRequest, sw);
            String text = sw.toString();
            soapBody.addTextNode(text);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return envelope;
    }

    public String callSoapWebService(String username, String password) {
        String response = "";

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpPost request = new HttpPost(soapEndpointUrl);
            request.setHeader("Content-Type", "text/xml");

            OutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);

            String envelope = outputStream.toString();
            envelope = envelope.replaceAll("&lt;", "<");
            envelope = envelope.replaceAll("&gt;", ">");
            StringEntity entity = new StringEntity(envelope);
            request.setEntity(entity);

            CloseableHttpResponse httpResponse = client.execute(request);
            response = httpResponse.toString();

        } catch (IOException | SOAPException e) {
            e.printStackTrace();
        }

        return response;
    }

    private SOAPMessage createSOAPRequest(String username, String password) throws Exception {
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        String userAndPassword = String.format("%s:%s", username, password);

        String basicAuth = Base64.getEncoder().encodeToString(userAndPassword.getBytes());
        headers.addHeader("Authorization", "Basic " + basicAuth);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }
}