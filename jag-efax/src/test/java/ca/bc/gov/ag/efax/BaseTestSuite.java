package ca.bc.gov.ag.efax;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import redis.embedded.RedisServer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestExecution;

import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import ca.bc.gov.ag.efax.mail.service.ExchangeServiceFactory;
import ca.bc.gov.ag.efax.redis.TestRedisConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestRedisConfiguration.class)
@ActiveProfiles("test")
public abstract class BaseTestSuite {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected RedisServer redisServer;

    @Autowired
    protected TestRestTemplate restTemplate;


    @Autowired
    protected SentMessageRepository sentMessageRepository;

    @MockBean
    private ExchangeServiceFactory exchangeServiceFactory;

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
