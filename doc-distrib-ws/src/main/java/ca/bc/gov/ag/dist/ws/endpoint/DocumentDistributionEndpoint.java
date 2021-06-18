package ca.bc.gov.ag.dist.ws.endpoint;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.bc.gov.ag.dist.mailservice.MailMessage;
import ca.bc.gov.ag.dist.mailservice.MailServiceApi;
import ca.bc.gov.ag.dist.ws.config.DocumentDistributionProperties;
import ca.bc.gov.ag.dist.ws.config.WebServiceConfig;
import ca.bc.gov.ag.dist.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.dist.ws.util.FaxUtils;

@Endpoint
public class DocumentDistributionEndpoint {

	private Logger logger = LoggerFactory.getLogger(DocumentDistributionEndpoint.class);

	@Autowired
	private DocumentDistributionProperties documentDistributionProperties;
	
	@Autowired
	private MailServiceApi mailServiceApi;
	
	@PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "DocumentDistributionRequest")
	public void initiate(@RequestPayload DocumentDistributionRequest request) {

		logger.trace("Request to initiate soap message, jobId: {}", request.getJobId());
		logRequest(request);
		
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
		
		logger.trace("Fax sent, jobId: {}", request.getJobId());
	}

	/** Logs the request for debugging purposes. */
	private void logRequest(DocumentDistributionRequest request) {
		try {
			String serialized = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request);
			logger.trace(serialized);
		} catch (JsonProcessingException e) {}
	}

}
