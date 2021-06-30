package ca.bc.gov.ag.dist.ws.endpoint;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.bc.gov.ag.dist.mailservice.MailServiceApi;
import ca.bc.gov.ag.dist.ws.config.DocumentDistributionProperties;
import ca.bc.gov.ag.dist.ws.config.WebServiceConfig;
import ca.bc.gov.ag.dist.ws.exception.CatchAllFault;
import ca.bc.gov.ag.dist.ws.exception.FAXSendFault;
import ca.bc.gov.ag.dist.ws.exception.FaxTransformationFault;
import ca.bc.gov.ag.dist.ws.exception.RuntimeFault;
import ca.bc.gov.ag.dist.ws.exception.UnknownChannelFault;
import ca.bc.gov.ag.dist.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.dist.ws.model.RequestChannel;
import ca.bc.gov.ag.dist.ws.util.FaxUtils;
import ca.bc.gov.ag.model.MailMessage;

@Endpoint
@EnableConfigurationProperties(DocumentDistributionProperties.class)
public class DocumentDistributionEndpoint {

	private Logger logger = LoggerFactory.getLogger(DocumentDistributionEndpoint.class);

	@Autowired
	private DocumentDistributionProperties documentDistributionProperties;
	
	@Autowired
	private MailServiceApi mailServiceApi;
	
	@PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "DocumentDistributionRequest")
	public void initiate(@RequestPayload DocumentDistributionRequest request) {

		String jobId = request.getJobId();
        logger.trace("Request to initiate soap message, jobId: {}", jobId);
		logRequest(request);
		
		if (!RequestChannel.FAX.getName().equals(request.getChannel())) {
		    throw new UnknownChannelFault(jobId, "Unrecognized channel - only 'fax' is implemented.");
		}

		try {
            
    		// DocumentDistributionMainProcess.bpel:generateInternalUUID
    		UUID uuid = UUID.randomUUID();
    
    		// DocumentDistributionMainProcess.bpel:constructFaxDestination
    		String faxDestination = FaxUtils.getFaxDestination(
    				documentDistributionProperties.getFaxFormat(),
    				request.getTo(),
    				request.getTransport());
    
    		// DocumentDistributionMainProcess.bpel:assignFinalFaxNumber
    		request.setTransport(faxDestination);
    
    		// DocumentDistributionMainProcess.bpel:prepareFaxMessage
    		// DocumentDistributionMainProcess.bpel:getAttachmentCount
    		// DocumentDistributionMainProcess.bpel:addAttachment
    		// DocumentDistributionMainProcess.bpel:setDocSentDateTimeStamp
    		MailMessage mailMessage = FaxUtils.prepareFaxMessage(uuid, request);
    		
    		// DocumentDistributionMainProcess.bpel:sendFax-Synch
    		mailServiceApi.sendMessage(mailMessage);

        } catch (FaxTransformationFault e) { // catch and re-throw with jobId
            throw new FaxTransformationFault(jobId, e);
        } catch (RuntimeFault e) {
            throw new RuntimeFault(jobId, e);
        } catch (FAXSendFault e) {
            throw new FAXSendFault(jobId, e);
        } catch (Exception e) {
            throw new CatchAllFault(jobId, e) ;
        }
		
		logger.trace("Fax sent, jobId: {}", jobId);
	}

	/** Logs the request for debugging purposes. */
	private void logRequest(DocumentDistributionRequest request) {
		try {
			String serialized = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request);
			logger.trace(serialized);
		} catch (JsonProcessingException e) {}
	}

}
