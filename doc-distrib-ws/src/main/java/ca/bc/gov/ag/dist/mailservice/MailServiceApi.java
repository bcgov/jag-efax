package ca.bc.gov.ag.dist.mailservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.dist.ws.exception.FAXSendFault;

@Service
@EnableConfigurationProperties(MailServiceProperties.class)
public class MailServiceApi {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MailServiceProperties mailServiceProperties;

	public ResponseEntity<Void> sendMessage(MailMessage mailMessage) {
	    try {
            return restTemplate.postForEntity(
	                mailServiceProperties.getMailApiBaseUrl() + "/sendMessage", mailMessage, Void.class);
        } catch (Exception e) {
            throw new FAXSendFault(e.getMessage());
        }
	}

}
