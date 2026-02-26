package io.github.wolfandw.mymarket.configuration;

import io.github.wolfandw.mymarket.model.Item;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

/**
 * Конфигурация настройки шаблонов Redis.
 */
@Configuration
public class RedisConfiguration {
    /**
     * Кэш для работы с товарами.
     *
     * @param connectionFactory фабрика соединений
     * @param objectMapper маппер
     * @return кэш товаров
     */
    @Bean
    public ReactiveRedisTemplate<String, Item> itemCacheTemplate(
            ReactiveRedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        JacksonJsonRedisSerializer<Item> valueSerializer =
                new JacksonJsonRedisSerializer<>(objectMapper, Item.class);

        RedisSerializationContext<String, Item> context = RedisSerializationContext.
                <String, Item>newSerializationContext(keySerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .key(keySerializer)
                .value(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    /**
     * Кэш количества товаров.
     *
     * @param connectionFactory фабрика соединений
     * @param objectMapper маппер
     * @return кэш количества товаров
     */
    @Bean
    public ReactiveRedisTemplate<String, Long> itemsCountCacheTemplate(
            ReactiveRedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        JacksonJsonRedisSerializer<Long> valueSerializer =
                new JacksonJsonRedisSerializer<>(objectMapper, Long.class);

        RedisSerializationContext<String, Long> context = RedisSerializationContext.
                <String, Long>newSerializationContext(keySerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .key(keySerializer)
                .value(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    /**
     * Кэш картинок сущностей.
     *
     * @param connectionFactory фабрика соединений
     * @return кэш картинок сущностей
     */
    @Bean
    public ReactiveRedisTemplate<String, byte[]> entityImageCacheTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        RedisSerializer<byte[]> valueSerializer = RedisSerializer.byteArray();

        RedisSerializationContext<String, byte[]> context = RedisSerializationContext.
                <String, byte[]>newSerializationContext(keySerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .key(keySerializer)
                .value(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
