package ca.bc.gov.ag.efax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import ca.bc.gov.ag.efax.redis.TestRedisConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestRedisConfiguration.class)
@ActiveProfiles("test")
public abstract class BaseTestSuite {

    @Autowired
    protected TestRestTemplate restTemplate;
    
}
