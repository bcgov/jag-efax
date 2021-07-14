package ca.bc.gov.ag.efax.ws.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.mail.util.DateUtil;
import ca.bc.gov.ag.efax.ws.exception.UnknownChannelFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest.Attachments;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

public class DocumentDistributionServiceTest extends BaseTestSuite {

    @Autowired
    private DocumentDistributionService documentDistributionService;

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

        // disable flattening service, return null
        when(pdfService.flattenPdf(any(), any())).thenReturn(null);
    }

    @Test
    public void testReceiveRequestToSendMessage() throws Exception {
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
    public void testReceiveMustBeAFax() throws DatatypeConfigurationException {
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
