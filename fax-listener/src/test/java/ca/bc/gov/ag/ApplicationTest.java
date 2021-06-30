package ca.bc.gov.ag;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import ca.bc.gov.ag.repository.SentMessageRepository;

public class ApplicationTest extends BaseTestSuite {

	@Autowired
	private TestRestTemplate restTemplate;

    @Autowired
    private SentMessageRepository sentMessageRepository;
	
	@Test
	public void contextLoads() {
		assertNotNull(sentMessageRepository);
	}

	@Test
	public void healthCheck() {
		// assert the health check returns { "status" : "UP" } - this confirms the ws is up.
		
		JsonNode node = restTemplate.getForObject("/actuator/health", JsonNode.class);
		assertTrue(Status.UP.getCode().equals(node.get("status").asText()));		
	}
	
}
