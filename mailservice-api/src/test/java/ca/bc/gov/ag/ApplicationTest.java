package ca.bc.gov.ag;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import ca.bc.gov.ag.mail.MailService;
import ca.bc.gov.ag.model.MailMessage;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private MailService mailService;
	
	@Test
	public void contextLoads() {
		assertNotNull(mailService);
	}

	@Test
	public void healthCheck() {
		// assert the health check returns { "status" : "UP" } - this confirms the ws is up.
		
		JsonNode node = restTemplate.getForObject("/actuator/health", JsonNode.class);
		assertTrue(Status.UP.getCode().equals(node.get("status").asText()));		
	}
	
	@Disabled
	@Test
	public void sendMessage() throws Exception {
		String testBody = "This is a test body42.";
		List<String> attachments = Arrays.asList( 
				"https://justice.gov.bc.ca/cso/about/efiling-info.pdf", // PDF Version 1.4
				"https://www2.gov.bc.ca/assets/gov/housing-and-tenancy/residential-tenancies/forms/rtb1.pdf" // PDF Version 1.6
		);		

		MailMessage mailMessage = new MailMessage();
		mailMessage.setUuid("3bcc44f035f92641:6e7bf1f2:116ab400d79:-7cb8");
		mailMessage.setTo("someone@somewhere.com");
		mailMessage.setSubject("Subject: Test43 from MailService");
		mailMessage.setBody(testBody);
		mailMessage.setAttachments(attachments);
		mailService.sendMessage(mailMessage);	
	}
	
}
