package ca.bc.gov.ag.efax.mail.service;

import java.net.URI;

import ca.bc.gov.ag.efax.mail.config.ExchangeProperties;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.WebCredentials;

public class ExchangeServiceFactory {

    private final ExchangeProperties exchangeProperties;

    public ExchangeServiceFactory(ExchangeProperties exchangeProperties) {
        this.exchangeProperties = exchangeProperties;
    }

    public ExchangeService createService() throws Exception {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        service.setUrl(new URI(exchangeProperties.getEndpoint()));
        String username = exchangeProperties.getUsername().replace("idir/", "");
        String password = exchangeProperties.getPassword();
        service.setCredentials(new WebCredentials(username, password));
        service.setTraceEnabled(true);
        return service;
    }
    
}
