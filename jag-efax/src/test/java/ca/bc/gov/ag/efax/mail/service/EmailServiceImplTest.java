package ca.bc.gov.ag.efax.mail.service;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.graph.service.MSGraphService;
import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import ca.bc.gov.ag.efax.mail.util.FaxUtils;
import ca.bc.gov.ag.efax.ws.TestUtils;
import ca.bc.gov.ag.efax.ws.config.JustinCallbackProperties;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.ag.efax.ws.service.DocumentDistributionServiceImpl;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.MessageCollectionResponse;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class EmailServiceImplTest extends BaseTestSuite {

    private EmailServiceImpl sut;

    @Mock
    private SentMessageRepository sentMessageRepositoryMock;

    @Mock
    private MSGraphService gServiceMock;

    private MailMessage mailMessageMock;

    @BeforeEach
    protected void beforeEach() throws Exception {
        MockitoAnnotations.openMocks(this);
        mailMessageMock = FaxUtils.prepareFaxMessage(UUID.fromString(UUID.randomUUID().toString()), TestUtils.getDocumentDistributionRequest());
        sut = new EmailServiceImpl(sentMessageRepositoryMock, gServiceMock);
    }

    @Test
    void withValidRequestShouldGetInboxEmails() throws Exception {
        MessageCollectionResponse response = new MessageCollectionResponse();
        List<Message> messages = new ArrayList<>();
        messages.add(TestUtils.getMessage());
        response.setValue(messages);
        when(gServiceMock.GetMessages()).thenReturn(response);
        sut.getInboxEmails();
    }

    @Test
    void withValidRequestShouldDeleteEmail() throws Exception {
        Mockito.doNothing().when(gServiceMock).deleteMessage(ArgumentMatchers.argThat( x -> x.equals(TestUtils.JOB_ID)));
        sut.deleteEmail(TestUtils.JOB_ID);
    }

    @Test
    void withValidRequestShouldSendMessage() throws Exception {
        when(sentMessageRepositoryMock.save(ArgumentMatchers.argThat( x -> x.getJobId().equals(TestUtils.JOB_ID)))).thenReturn(Mockito.any());
        Assertions.assertDoesNotThrow( () -> sut.sendMessage(mailMessageMock));
    }

    @Test
    void withInValidRequestShouldFailSendMessage() throws Exception {
        MailMessage mailMessage = FaxUtils.prepareFaxMessage(UUID.fromString(UUID.randomUUID().toString()), TestUtils.getDocumentDistributionRequest());
        doThrow(NullPointerException.class).when(sentMessageRepositoryMock).save(Mockito.any(SentMessage.class));
        Assertions.assertThrows(FAXSendFault.class, () -> sut.sendMessage(mailMessage));
    }
}