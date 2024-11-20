package ca.bc.gov.ag.efax.ws.endpoint;

import static org.mockito.Mockito.*;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.ws.TestUtils;
import ca.bc.gov.ag.efax.ws.exception.FaxTransformationFault;
import ca.bc.gov.ag.efax.ws.service.DocumentDistributionService;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;

public class DocumentDistributionEndpointTest extends BaseTestSuite {
    private DocumentDistributionEndpoint sut;
    @MockBean
    private DocumentDistributionService documentDistributionService;
    @BeforeEach
    protected void beforeEach() throws Exception {
        sut = new DocumentDistributionEndpoint(documentDistributionService);

    }

    @Test
    void testInitiateEndpoint_FaxTransformationSuccess() throws DatatypeConfigurationException {
        DocumentDistributionRequest request = TestUtils.getDocumentDistributionRequest();
        Mockito.doNothing().when(documentDistributionService).initiateRequestToSendMessage(Mockito.eq(request));
        sut.initiate(request);
    }

    @Test
    void testInitiateEndpoint_FaxTransformationFault() throws DatatypeConfigurationException {
        DocumentDistributionRequest request = TestUtils.getDocumentDistributionRequest();
        doThrow(FAXSendFault.class).when(documentDistributionService).initiateRequestToSendMessage(Mockito.eq(request));
        Assertions.assertThrows(FAXSendFault.class, () -> {
            sut.initiate(request);
        });
    }

    @Test
    void testInitiateEndpoint_FAXSendFault() throws DatatypeConfigurationException {
        DocumentDistributionRequest request = TestUtils.getDocumentDistributionRequest();
        doThrow(FaxTransformationFault.class).when(documentDistributionService).initiateRequestToSendMessage(Mockito.eq(request));
        Assertions.assertThrows(FaxTransformationFault.class, () -> {
            sut.initiate(request);
        });
    }
}
