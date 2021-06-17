package ca.bc.gov.ag;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.bc.gov.ag.mail.MailService;

@SpringBootTest
public class ApplicationTest {

	@Autowired
	private MailService mailService;
	
	@Test
	public void contextLoads() {
		assertNotNull(mailService);
	}
	
	@Disabled
	@Test
	public void sendMessage() throws Exception {
		String testBody = "This is a test body42.";
		URI[] attachments = new URI[] { 
				new URI("https://justice.gov.bc.ca/cso/about/efiling-info.pdf"), // PDF Version 1.4
				new URI("https://www2.gov.bc.ca/assets/gov/housing-and-tenancy/residential-tenancies/forms/rtb1.pdf") // PDF Version 1.6
		};

		mailService.sendMessage(
				"3bcc44f035f92641:6e7bf1f2:116ab400d79:-7cb8", 
				"someone@somewhere.com",
				"Subject: Test42 from MailService", 
				testBody, 
				attachments);	
	}
	
}
