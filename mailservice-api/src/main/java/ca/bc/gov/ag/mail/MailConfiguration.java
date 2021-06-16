package ca.bc.gov.ag.mail;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfiguration {

	@Bean
	public MailProperties mailProperties(MailProperties mailProperties) {
		return mailProperties;
	}
}
