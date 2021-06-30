package ca.bc.gov.ag.dist.mailservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.dist.BaseTestSuite;
import ca.bc.gov.ag.model.MailMessage;

class MailServiceTest extends BaseTestSuite {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private MailServiceProperties mailServiceProperties;

	@InjectMocks
	private MailServiceApi mailServiceApi = new MailServiceApi();

	@BeforeEach
	private void setUp() {
		when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.eq(Void.class)))
				.thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
	}

	@Test
	public void testSendMessage() {
		MailMessage mailMessage = new MailMessage();
		mailMessage.setUuid(UUID.randomUUID().toString());
		mailMessage.setTo("somebody@somewhere.com");
		mailMessage.setBody("Test body");
		mailMessage.setSubject("Test subject");
		mailMessage.setAttachments(new ArrayList<String>());
		ResponseEntity<Void> response = mailServiceApi.sendMessage(mailMessage);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

}
