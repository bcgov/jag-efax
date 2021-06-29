package ca.bc.gov.ag;

import org.springframework.boot.test.context.SpringBootTest;

import ca.bc.gov.ag.redis.TestRedisConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestRedisConfiguration.class)
public class BaseTestSuite {

}
