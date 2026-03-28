package ru.ugaforever.reactive.market.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import redis.embedded.RedisServer;

import java.io.IOException;

@TestConfiguration
public class EmbeddedRedisConfiguration {

    private static final int REDIS_PORT = 6379;

    @Bean(destroyMethod = "stop")
    public RedisServer redisServer() throws IOException {
        RedisServer redisServer = new RedisServer( REDIS_PORT);
        redisServer.start();
        return redisServer;
    }
}
