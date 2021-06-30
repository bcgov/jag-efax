package ca.bc.gov.ag.dist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import ca.bc.gov.ag.dist.redis.TestRedisConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestRedisConfiguration.class)
public abstract class BaseTestSuite {

	@Autowired
	protected TestRestTemplate restTemplate;
	
}
