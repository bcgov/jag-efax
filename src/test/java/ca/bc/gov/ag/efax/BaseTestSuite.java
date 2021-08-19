package ca.bc.gov.ag.efax;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestExecution;
import org.springframework.ws.test.server.MockWebServiceClient;

import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import ca.bc.gov.ag.efax.mail.service.ExchangeServiceFactory;
import ca.bc.gov.ag.efax.redis.TestRedisConfiguration;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.response.CreateAttachmentResponse;
import microsoft.exchange.webservices.data.core.response.ServiceResponseCollection;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import redis.embedded.RedisServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestRedisConfiguration.class)
@ActiveProfiles("test")
public abstract class BaseTestSuite {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected RedisServer redisServer;

    @Autowired
    protected TestRestTemplate restTemplate;

    @MockBean
    protected ExchangeService exchangeService;

    @Autowired
    protected SentMessageRepository sentMessageRepository;

    @MockBean
    private ExchangeServiceFactory exchangeServiceFactory;

    protected MockWebServiceClient mockClient;
    
    @BeforeEach
    protected void beforeEach() throws Exception {
        if (!redisServer.isActive()) {
            try {
                logger.trace("Redis is starting...");
                redisServer.start();
            }
            catch (Exception e) {
                // already running.
            }
        }
        
        when(exchangeServiceFactory.createService()).thenReturn(exchangeService);

        // mock basic exchangeService methods
        when(exchangeService.getRequestedServerVersion()).thenReturn(ExchangeVersion.Exchange2010_SP2);
        when(exchangeService.createAttachments(any(), any())).thenReturn(new ServiceResponseCollection<CreateAttachmentResponse>());

        // exchangeService - add a generated UUID when "saving" an email message
        doAnswer(invocation -> {
            EmailMessage emailMessage = invocation.getArgument(0);
            emailMessage.getPropertyBag().getProperties().put(emailMessage.getIdPropertyDefinition(), new ItemId(UUID.randomUUID().toString()));
            return null;
        }).when(exchangeService).createItem(any(), any(), any(), any());

        // clearout redis from any previous test data
        sentMessageRepository.deleteAll();
    }

    @AfterTestExecution
    protected void afterTestExecution() throws Exception {
        try {
            logger.trace("Redis is stopping...");
            redisServer.stop();
        } catch (Exception e) {
        }
    }
}
