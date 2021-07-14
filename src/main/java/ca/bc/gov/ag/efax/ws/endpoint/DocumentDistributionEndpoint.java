package ca.bc.gov.ag.efax.ws.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.bc.gov.ag.efax.ws.config.DocumentDistributionProperties;
import ca.bc.gov.ag.efax.ws.config.WebServiceConfig;
import ca.bc.gov.ag.efax.ws.exception.CatchAllFault;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.ag.efax.ws.exception.FaxTransformationFault;
import ca.bc.gov.ag.efax.ws.exception.RuntimeFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.efax.ws.service.DocumentDistributionService;

@Endpoint
@EnableConfigurationProperties(DocumentDistributionProperties.class)
public class DocumentDistributionEndpoint {

    private Logger logger = LoggerFactory.getLogger(DocumentDistributionEndpoint.class);

    @Autowired
    private DocumentDistributionService documentDistributionService;

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "DocumentDistributionRequest")
    public void initiate(@RequestPayload DocumentDistributionRequest request) {

        String jobId = request.getJobId();
        logger.debug("Request to initiate soap message, jobId: {}", jobId);
        logRequest(request);

        try {

            documentDistributionService.receiveRequestToSendMessage(request);

        } catch (FaxTransformationFault e) { // catch and re-throw with jobId
            throw new FaxTransformationFault(jobId, e);
        } catch (RuntimeFault e) {
            throw new RuntimeFault(jobId, e);
        } catch (FAXSendFault e) {
            throw new FAXSendFault(jobId, e);
        } catch (Exception e) {
            throw new CatchAllFault(jobId, e);
        }

        logger.debug("Fax sent, jobId: {}", jobId);
    }

    /** Logs the request for debugging purposes. */
    private void logRequest(DocumentDistributionRequest request) {
        try {
            String serialized = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request);
            logger.trace(serialized);
        } catch (JsonProcessingException e) {
        }
    }

}
