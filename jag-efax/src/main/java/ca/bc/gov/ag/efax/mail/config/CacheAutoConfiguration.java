package ca.bc.gov.ag.efax.mail.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;


@Configuration
@ComponentScan
public class CacheAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(CacheAutoConfiguration.class);

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisProperties properties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(properties.getHost());
        redisStandaloneConfiguration.setPort(properties.getPort());
        redisStandaloneConfiguration.setPassword(properties.getPassword());

        logger.info("host: " + properties.getHost() + " post: " + properties.getPort() + " password: " + properties.getPassword() );
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
}
