package ca.bc.gov.ag;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import ca.bc.gov.ag.fl.config.ExchangeProperties;
import ca.bc.gov.ag.fl.config.ExchangeServiceFactory;

@Configuration
@EnableConfigurationProperties({ExchangeProperties.class})
@EnableScheduling
public class AutoConfiguration {

    @Bean
    public ExchangeServiceFactory exchangeConfig(ExchangeProperties exchangeProperties) {
        return new ExchangeServiceFactory(exchangeProperties);
    }
    
}
