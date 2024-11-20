package ca.bc.gov.ag.efax.ws.service;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.mail.scheduled.EmailPoller;
import ca.bc.gov.ag.efax.mail.service.EmailService;
import ca.bc.gov.ag.efax.ws.TestUtils;
import ca.bc.gov.ag.efax.ws.config.DocumentDistributionProperties;
import ca.bc.gov.ag.efax.ws.config.JustinCallbackProperties;
import ca.bc.gov.ag.efax.ws.endpoint.DocumentDistributionEndpoint;
import ca.bc.gov.ag.efax.ws.exception.UnknownChannelFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.mockito.Mockito.*;

class DocumentDistributionServiceImplTest extends BaseTestSuite {

    @MockBean
    private DocumentDistributionProperties documentDistributionPropertiesMock;

    @MockBean
    private EmailService emailServiceMock;

    @MockBean
    private DocumentDistributionServiceImpl sut;

    @Mock
    private WebServiceTemplate webServiceTemplateMock;

    @BeforeEach
    protected void beforeEach() throws Exception {
        MockitoAnnotations.openMocks(this);
        JustinCallbackProperties justinCallbackProperties = new JustinCallbackProperties();
        justinCallbackProperties.setEnabled(true);
        justinCallbackProperties.setEndpoint("TEST");
        when(documentDistributionPropertiesMock.getCallback()).thenReturn(justinCallbackProperties);
        when(documentDistributionPropertiesMock.getFaxFormat()).thenReturn("%FAXNUMBER%@fax.extest.gov.bc.ca");
        sut = new DocumentDistributionServiceImpl(documentDistributionPropertiesMock, emailServiceMock, new Jaxb2Marshaller());
        sut.setWebServiceTemplate(webServiceTemplateMock);
    }

    @Test
    void initWebServiceTemplateForUnitTest() throws DatatypeConfigurationException {
        Assertions.assertDoesNotThrow( () -> sut.initWebServiceTemplateForUnitTest(webServiceTemplateMock));
    }

    @Test
    void withValidRequestShouldSendMessage() throws DatatypeConfigurationException {
        DocumentDistributionRequest request = TestUtils.getDocumentDistributionRequest();
        Mockito.doNothing().when(emailServiceMock).sendMessage(Mockito.any(MailMessage.class));
        Assertions.assertDoesNotThrow( () -> sut.initiateRequestToSendMessage(request));
    }

    @Test
    void withInValidChannalRequestShouldFailSendMessage() throws DatatypeConfigurationException {
        DocumentDistributionRequest request = TestUtils.getDocumentDistributionRequest();
        request.setChannel("non-fax");
        Mockito.doNothing().when(emailServiceMock).sendMessage(Mockito.any(MailMessage.class));
        Assertions.assertThrows(UnknownChannelFault.class, () -> sut.initiateRequestToSendMessage(request));
    }

    @Test
    void withValidRequestShouldSendResponseToCallback() {
        DocumentDistributionMainProcessProcessResponse req =  new DocumentDistributionMainProcessProcessResponse();
        when(webServiceTemplateMock.marshalSendAndReceive(Mockito.any(DocumentDistributionMainProcessProcessResponse.class))).thenReturn(Object.class);
        Assertions.assertDoesNotThrow( () -> sut.sendResponseToCallback(req));
    }

    @Test
    void withCallbackDisableRequestShouldNotBreakSendResponseToCallback() {
        JustinCallbackProperties justinCallbackProperties = new JustinCallbackProperties();
        justinCallbackProperties.setEnabled(false);
        justinCallbackProperties.setEndpoint("TEST");
        when(documentDistributionPropertiesMock.getCallback()).thenReturn(justinCallbackProperties);

        DocumentDistributionMainProcessProcessResponse req =  new DocumentDistributionMainProcessProcessResponse();
        when(webServiceTemplateMock.marshalSendAndReceive(Mockito.any(DocumentDistributionMainProcessProcessResponse.class))).thenReturn(Object.class);
        Assertions.assertDoesNotThrow( () -> sut.sendResponseToCallback(req));
    }

    @Test
    void withInvalidWebRequestShouldNotBreakSendResponseToCallback() {
        DocumentDistributionMainProcessProcessResponse req =  new DocumentDistributionMainProcessProcessResponse();
        when(webServiceTemplateMock.marshalSendAndReceive(Mockito.any(DocumentDistributionMainProcessProcessResponse.class))).thenThrow(RuntimeException.class);
        Assertions.assertDoesNotThrow( () -> sut.sendResponseToCallback(req));
    }

}