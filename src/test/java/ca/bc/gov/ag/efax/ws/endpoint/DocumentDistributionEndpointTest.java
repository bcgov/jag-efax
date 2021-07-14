package ca.bc.gov.ag.efax.ws.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.serverOrReceiverFault;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.Source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.test.server.MockWebServiceClient;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.mail.util.DateUtil;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest.Attachments;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

public class DocumentDistributionEndpointTest extends BaseTestSuite {

    @Autowired
    private ApplicationContext applicationContext;

    private MockWebServiceClient mockClient;
    private List<EmailMessage> emails;

    @BeforeEach
    @Override
    protected void beforeEach() throws Exception {
        super.beforeEach();
        
        mockClient = MockWebServiceClient.createClient(applicationContext);

        // An in-memory cache of "sent" emails by the ExchangeService
        emails = new ArrayList<EmailMessage>();

        // exchangeService - add the email to the List<EmailMessage> when "sending" an email.
        doAnswer(invocation -> {
            EmailMessage emailMessage = invocation.getArgument(0);
            emails.add(emailMessage);
            return null;
        }).when(exchangeService).sendItem(any(), any());

        sentMessageRepository.deleteAll();
    }

    @Test
    void testInitiateEndpoint() throws Exception {        
        DocumentDistributionRequest request = getBaseRequest();
        
        assertEquals(0, emails.size());
        assertEquals(0, sentMessageRepository.count());   
        mockClient.sendRequest(withPayload(getPayload(request))).andExpect(noFault());
        assertEquals(1, emails.size());
        assertEquals(1, sentMessageRepository.count());   
    }

    @Test
    void testInitiateEndpoint_FaxTransformationFault() throws Exception {        
        DocumentDistributionRequest request = getBaseRequest();
        request.setTransport("");
        
        assertEquals(0, emails.size());
        assertEquals(0, sentMessageRepository.count());   
        mockClient.sendRequest(withPayload(getPayload(request))).andExpect(serverOrReceiverFault("ca.bc.gov.ag.efax.ws.exception.FaxTransformationFault: No digits in fax number."));
        assertEquals(0, emails.size());
        assertEquals(0, sentMessageRepository.count());   
    }

    @Test
    void testInitiateEndpoint_FAXSendFault() throws Exception {    
        // pretend exchangeServer is offline
        doAnswer(invocation -> {
            throw new FAXSendFault("Offline");
        }).when(exchangeService).sendItem(any(), any());
        
        DocumentDistributionRequest request = getBaseRequest();
        
        assertEquals(0, emails.size());
        assertEquals(0, sentMessageRepository.count());   
        mockClient.sendRequest(withPayload(getPayload(request))).andExpect(serverOrReceiverFault("ca.bc.gov.ag.efax.ws.exception.FAXSendFault: ca.bc.gov.ag.efax.ws.exception.FAXSendFault: Offline"));
        assertEquals(0, emails.size());
        assertEquals(0, sentMessageRepository.count());   
    }

    private Source getPayload(DocumentDistributionRequest request) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(DocumentDistributionRequest.class);
        Source payload = new JAXBSource(jc, request);
        return payload;
    }

    private DocumentDistributionRequest getBaseRequest() throws DatatypeConfigurationException {
        DocumentDistributionRequest request = new DocumentDistributionRequest();
        request.setFrom("Someone");
        request.setTo("Someone Else");
        request.setJobId("99999");
        request.setDateTime(DateUtil.toXMLGregorianCalendar(new Date()));
        request.setTimeout(DatatypeFactory.newInstance().newDuration(1500000));
        request.setChannel("fax");
        request.setTransport("5551234567");
        request.setSubject("Test subject");
        request.setBody("Test body");
        request.setNumPages(1);
        request.setAttachments(new Attachments());
        request.getAttachments().getUri().add("https://github.com/bcgov/jag-efax/raw/main/src/test/resources/sample_v1.6.pdf");
        request.setExtension1("a");
        request.setExtension2("b");
        return request;
    }
    
    

}
