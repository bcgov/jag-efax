package ca.bc.gov.ag.efax.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfiguration {
    
    @Bean
    public RedisServer redisServer(RedisProperties redisProperties) {
        return new RedisServer(redisProperties.getPort());
    }
    
}
