package io.github.wolfandw.mymarket.itest.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import redis.embedded.RedisServer;

import java.io.IOException;

/**
 * Конфигурация для тестов, использующих embedded-redis
 */
@TestConfiguration
public class EmbeddedRedisConfiguration {
    /**
     * Создаёт {@link RedisServer}
     */
    @Bean(destroyMethod = "stop")
    public RedisServer redisServer() throws IOException {
        RedisServer redisServer = new RedisServer(6378);
        redisServer.start();
        return redisServer;
    }
}
