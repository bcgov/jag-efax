package ca.bc.gov.ag.efax.mail.scheduled;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import ca.bc.gov.ag.efax.graph.model.GmailMessage;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.*;

import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;
import ca.bc.gov.ag.efax.mail.service.EmailService;
import ca.bc.gov.ag.efax.mail.service.parser.EmailParser;
import ca.bc.gov.ag.efax.ws.service.DocumentDistributionService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailPollerTest {
    private EmailPoller emailPollerMock;
    @Mock
    private EmailService emailServiceMock;
    @Mock
    private EmailParser emailParserMock;
    @Mock
    private DocumentDistributionService documentDistributionServiceMock;
    @Mock
    private SentMessageRepository sentMessageRepositoryMock;

    @BeforeAll
    public void beforeAll() {
        MockitoAnnotations.openMocks(this);
        emailPollerMock = new EmailPoller(emailServiceMock, emailParserMock, documentDistributionServiceMock, sentMessageRepositoryMock);
    }
    
    @Test
    void testExpectedMsgRemovedFromQueue() throws Exception {
        GmailMessage email = new GmailMessage();
        UUID uuid = UUID.randomUUID();
        email.setId(uuid.toString());
        email.setSubject("Message Succeeded: <jobId>1234</jobId><uuid>aabbcc</uuid>.");
        List<GmailMessage> emails = Arrays.asList(email);
        when(emailServiceMock.getInboxEmails()).thenReturn(emails);

        DocumentDistributionMainProcessProcessResponseDecorator result = new DocumentDistributionMainProcessProcessResponseDecorator();
        result.setUuid(uuid.toString());
        result.setJobId("1234");
        when(emailParserMock.parse(email)).thenReturn(result);

        Mockito.doNothing().when(sentMessageRepositoryMock).deleteById(ArgumentMatchers.eq(uuid.toString()));
        Mockito.doNothing().when(emailServiceMock).deleteEmail(ArgumentMatchers.eq(uuid.toString()));

        // trigger the scheduled task
        emailPollerMock.pollForEmails();
    }
    
}
