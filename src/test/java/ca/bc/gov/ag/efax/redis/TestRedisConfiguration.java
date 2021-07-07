package ca.bc.gov.ag.efax.redis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;

import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfiguration {

    private Logger logger = LoggerFactory.getLogger(TestRedisConfiguration.class);
    
    private RedisServer redisServer;

    public TestRedisConfiguration(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getPort());
    }

    @PostConstruct
    public void postConstruct() {
        logger.debug("Redis starting up ...");
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        logger.debug("Redis shutting down ...");
        redisServer.stop();
    }
}
