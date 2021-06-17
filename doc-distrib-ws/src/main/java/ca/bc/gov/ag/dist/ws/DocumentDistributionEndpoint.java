package ca.bc.gov.ag.dist.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Endpoint
public class DocumentDistributionEndpoint {
	
	private Logger logger = LoggerFactory.getLogger(DocumentDistributionEndpoint.class);

	@PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "DocumentDistributionRequest")
	@ResponsePayload
	public DocumentDistributionMainProcessProcessResponse getCountry(@RequestPayload DocumentDistributionRequest request) {
		DocumentDistributionMainProcessProcessResponse response = new DocumentDistributionMainProcessProcessResponse();

		try {
			String serialized = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request);
			logger.trace(serialized);
		} catch (JsonProcessingException e) {}
		
		return response;
	}
	
}
