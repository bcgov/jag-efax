package ca.bc.gov.ag.efax.mail.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.bc.gov.ag.efax.mail.service.ExchangeServiceFactory;

@Configuration
@EnableConfigurationProperties(ExchangeProperties.class)
public class ExchangeConfiguration {

	@Bean
	public ExchangeProperties exchangeProperties(ExchangeProperties exchangeProperties) {
		return exchangeProperties;
	}

    @Bean
    public ExchangeServiceFactory exchangeConfig(ExchangeProperties exchangeProperties) {
        return new ExchangeServiceFactory(exchangeProperties);
    }
	
}
