package ca.bc.gov.ag.efax.mail.service;

import java.net.MalformedURLException;
import java.net.URI;

import javax.xml.rpc.ServiceException;

import ca.bc.gov.ag.efax.mail.config.ExchangeProperties;
import ca.bc.gov.jag.ews.proxy.ExchangeWebServiceClient;
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
        service.setCredentials(new WebCredentials(
                exchangeProperties.getUsername().replace("idir/", ""), 
                exchangeProperties.getPassword()));
        service.setTraceEnabled(true);
        return service;
    }

    public ExchangeWebServiceClient createClient() throws MalformedURLException, ServiceException {
        ExchangeWebServiceClient client = new ExchangeWebServiceClient(
                exchangeProperties.getEndpoint(),
                exchangeProperties.getUsername(), 
                exchangeProperties.getPassword());
        return client;
    }
    
}
