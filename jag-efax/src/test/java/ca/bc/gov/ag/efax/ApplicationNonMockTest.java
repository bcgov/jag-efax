package ca.bc.gov.ag.efax;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.xml.ws.Dispatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ca.bc.gov.ag.efax.mail.config.ExchangeProperties;
import ca.bc.gov.ag.efax.mail.service.ExchangeServiceFactory;
import ca.bc.gov.ag.efax.redis.TestRedisConfiguration;
import microsoft.exchange.webservices.data.core.ExchangeService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestRedisConfiguration.class)
@ActiveProfiles("test")
public class ApplicationNonMockTest {

    @Autowired
    protected Dispatch<Object> outputServiceDispatch;

    @Autowired
    ExchangeProperties exchangeProperties;
    
	@Test
	public void contextLoads() throws Exception {
        assertNotNull(outputServiceDispatch);
        assertNotNull(exchangeProperties);
        
        ExchangeServiceFactory factory = new ExchangeServiceFactory(exchangeProperties);
        ExchangeService exchangeService = factory.createService();
        assertNotNull(exchangeService);
        
	}
	
}
