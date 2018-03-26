package com.microservices.support.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
//    For cluster
//    @Autowired
//    ClusterConfigurationProperties clusterProperties;
//
//    @Bean
//    public RedisConnectionFactory connectionFactory() {
//        return new JedisConnectionFactory(new RedisClusterConfiguration(clusterProperties.getNodes()));
//    }

    @Bean
    @Primary
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean(name = "cacheRedisConnFactory")
    JedisConnectionFactory jedisConnectionFactory2() {
        JedisConnectionFactory jedisConFactory
                = new JedisConnectionFactory();
        jedisConFactory.setHostName("localhost");
        jedisConFactory.setPort(6379);
        return jedisConFactory;
    }

//    @Bean(name = "cacheRedisTemplate")
//    public RedisTemplate<String, Object> redisTemplate(@Qualifier("cacheRedisConnFactory") RedisConnectionFactory rcf) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(rcf);
//        return template;
//    }

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory rcf) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(rcf);
        StringRedisTemplate sTemplate = new StringRedisTemplate();
        sTemplate.setConnectionFactory(rcf);
        return sTemplate;
    }

//    @Bean
//    public CacheManager cacheManager() {
//        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//        return cacheManager;
//    }

}
