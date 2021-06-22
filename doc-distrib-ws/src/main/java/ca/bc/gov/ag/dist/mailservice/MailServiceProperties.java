package ca.bc.gov.ag.dist.mailservice;


import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "mailservice")
public class MailServiceProperties {

	@NotEmpty
	private String mailApiBaseUrl;
	
	public String getMailApiBaseUrl() {
		return mailApiBaseUrl;
	}
	
	public void setMailApiBaseUrl(String mailApiBaseUrl) {
		this.mailApiBaseUrl = mailApiBaseUrl;
	}
	
}
