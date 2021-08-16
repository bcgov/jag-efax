package ca.bc.gov.ag.efax.mail.scheduled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;
import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.mail.service.EmailService;
import ca.bc.gov.ag.efax.mail.service.parser.EmailParser;
import ca.bc.gov.ag.efax.mail.util.DateUtil;
import ca.bc.gov.ag.efax.ws.service.DocumentDistributionService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

public class EmailPollerTest extends BaseTestSuite {
    
    @InjectMocks
    private EmailPoller emailPoller;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailParser emailParser;

    @Mock
    private DocumentDistributionService documentDistributionService;
    
    @BeforeEach
    protected void beforeEach() throws Exception {
        super.beforeEach();
    }
    
    @Test
    void testExpectedMsgRemovedFromQueue() throws Exception {
        EmailMessage email = new EmailMessage(exchangeService);
        email.setSubject("Message Succeeded: <jobId>1234</jobId><uuid>aabbcc</uuid>.");
        List<EmailMessage> emails = Arrays.asList(email);        
        when(emailService.getInboxEmails()).thenReturn(emails);
        
        DocumentDistributionMainProcessProcessResponseDecorator result = new DocumentDistributionMainProcessProcessResponseDecorator();
        result.setUuid(UUID.randomUUID().toString());
        result.setJobId("1234");
        when(emailParser.parse(email)).thenReturn(result);
        
        // seed the repo with a single value - an expected email with the given jobId somewhere in the subject or body.
        SentMessage expectedMsg = new SentMessage();
        expectedMsg.setJobId(result.getJobId());
        expectedMsg.setUuid(result.getUuid());
        expectedMsg.setCreatedTs(DateUtil.addMinutes(new Date(), -10));
        sentMessageRepository.save(expectedMsg);
        assertEquals(1, sentMessageRepository.count());  
        
        // trigger the scheduled task
        emailPoller.pollForEmails();

        // assert after polling, the previously inserted redis queue record has been removed.
        assertEquals(1, sentMessageRepository.count()); 
    }
    
}
