package ca.bc.gov.ag.efax.mail.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.bc.gov.ag.efax.mail.service.ExchangeServiceFactory;
import ca.bc.gov.ag.efax.mail.service.parser.EmailParser;
import ca.bc.gov.ag.efax.mail.service.parser.UndeliverableVisitor;

@Configuration
@EnableConfigurationProperties(ExchangeProperties.class)
public class ExchangeConfiguration {

    @Bean
    public ExchangeServiceFactory exchangeConfig(ExchangeProperties exchangeProperties) {
        return new ExchangeServiceFactory(exchangeProperties);
    }

    @Bean
    public EmailParser emailParser() {
        EmailParser emailParser = new EmailParser();
        emailParser.registerVisitor(new UndeliverableVisitor());
        return emailParser;
    }

}
