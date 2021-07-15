package ca.bc.gov.ag.efax.ws.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.ws.test.client.RequestMatchers.payload;
import static org.springframework.ws.test.client.ResponseCreators.withException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.Source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.mail.util.DateUtil;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.ag.efax.ws.exception.UnknownChannelFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest.Attachments;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

public class DocumentDistributionServiceTest extends BaseTestSuite {

    @Autowired
    private DocumentDistributionService documentDistributionService;

    private MockWebServiceServer mockServer;      
    private List<EmailMessage> emails;

    @BeforeEach
    @Override
    protected void beforeEach() throws Exception {
        super.beforeEach();

        // An in-memory cache of "sent" emails by the ExchangeService
        emails = new ArrayList<EmailMessage>();

        // exchangeService - add the email to the List<EmailMessage> when "sending" an email.
        doAnswer(invocation -> {
            EmailMessage emailMessage = invocation.getArgument(0);
            emails.add(emailMessage);
            return null;
        }).when(exchangeService).sendItem(any(), any());

        mockServer = MockWebServiceServer.createServer((WebServiceGatewaySupport) documentDistributionService);
    }

    @Test
    public void testInitiateRequestToSendMessage() throws Exception {
        // Happy path - sending a correctly formated request should 
        // - "send" an email
        // - add a waiting queue record to redis

        // assert emails and redis are empty
        assertEquals(0, emails.size());
        assertEquals(0, sentMessageRepository.count());       
        
        documentDistributionService.initiateRequestToSendMessage(createBaseRequest());

        // assert emails and redis now have 1 record each.
        assertEquals(1, emails.size());
        assertEquals(1, sentMessageRepository.count());      
        
        assertEquals("Body text", emails.get(0).getBody().toString());
        assertEquals(1, emails.get(0).getAttachments().getCount());
    }

    @Test
    public void testInitiateUnknownChannelFault() throws DatatypeConfigurationException {
        // Attempt to send a message of type "email" - should fail as only "fax" is implemented.

        DocumentDistributionRequest request = createBaseRequest();
        request.setChannel("email");
        try {
            documentDistributionService.initiateRequestToSendMessage(request);
            fail("Channel must be set to fax and only fax.");
        } catch (UnknownChannelFault e) {
        }

        // assert no emails sent
        assertEquals(0, emails.size());
    }

    @Test
    public void testInitiateFaxSendFault() throws Exception {
        // Attempt to send a message to a broken or offline exchange server - should result in a FAXSendFault

        // pretend exchangeServer is offline
        doAnswer(invocation -> {
            throw new FAXSendFault("Offline");
        }).when(exchangeService).sendItem(any(), any());
        
        DocumentDistributionRequest request = createBaseRequest();
        try {
            documentDistributionService.initiateRequestToSendMessage(request);
            fail("Expected FAXSendFault since ExchangeServer is offline.");
        } catch (FAXSendFault e) {
        } catch (Exception e) {
            fail("Unexpected Exception thrown.", e);
        } 

        // assert no emails sent
        assertEquals(0, emails.size());
    }

    @Test
    public void testSendResponseToCallback_valid() {
        DocumentDistributionMainProcessProcessResponse response = new DocumentDistributionMainProcessProcessResponse();
        response.setJobId("12345");
        response.setStatusCode("AAA");
        response.setStatusMessage("Some status message");

        // correct webservice payload
        StringBuffer sb = new StringBuffer();
        sb.append("<ns2:DocumentDistributionMainProcessProcessResponse xmlns:ns2=\"http://ag.gov.bc.ca/DocumentDistributionMainProcess\">");
        sb.append("<ns2:jobId>12345</ns2:jobId>");
        sb.append("<ns2:statusCode>AAA</ns2:statusCode>");
        sb.append("<ns2:statusMessage>Some status message</ns2:statusMessage>");
        sb.append("</ns2:DocumentDistributionMainProcessProcessResponse>");
        Source source = new StringSource(sb.toString());
        mockServer.expect(payload(source));

        // no error expected.
        documentDistributionService.sendResponseToCallback(response);        
    }

    @Test
    public void testSendResponseToCallback_invalid() {
        DocumentDistributionMainProcessProcessResponse response = new DocumentDistributionMainProcessProcessResponse();
        response.setJobId("12345");
        response.setStatusCode("AAA");
        response.setStatusMessage("Some status message");

        // correct webservice payload, but respond with an exception.
        StringBuffer sb = new StringBuffer();
        sb.append("<ns2:DocumentDistributionMainProcessProcessResponse xmlns:ns2=\"http://ag.gov.bc.ca/DocumentDistributionMainProcess\">");
        sb.append("<ns2:jobId>12345</ns2:jobId>");
        sb.append("<ns2:statusCode>AAA</ns2:statusCode>");
        sb.append("<ns2:statusMessage>Some status message</ns2:statusMessage>");
        sb.append("</ns2:DocumentDistributionMainProcessProcessResponse>");
        Source source = new StringSource(sb.toString());
        mockServer.expect(payload(source)).andRespond(withException(new RuntimeException()));

        // no error expected since it's quietly suppressed
        documentDistributionService.sendResponseToCallback(response);
    }

    /** Creates a basic fax request message. */
    private DocumentDistributionRequest createBaseRequest() throws DatatypeConfigurationException {
        DocumentDistributionRequest request = new DocumentDistributionRequest();
        request.setJobId("12345");
        request.setTo("Someone at The Office");
        request.setSubject("Subject text");
        request.setBody("Body text");
        request.setChannel("Fax");
        request.setTransport("2505551234");
        request.setAttachments(new Attachments());
        request.getAttachments().getUri().add(getClass().getClassLoader().getResource("sample_v1.6.pdf").toString());
        request.setDateTime(DateUtil.toXMLGregorianCalendar(new Date()));
        return request;
    }

}
