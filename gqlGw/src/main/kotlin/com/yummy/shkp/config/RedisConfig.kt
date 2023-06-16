package com.yummy.shkp.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.BatchStrategies
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Configuration
@EnableCaching
class RedisConfig {

    data class Topic(
        val name: String,
        val duration: Duration?
    )

    var namespace: String = "yummyTest"
    var batchSize: Int = 1000

    @Bean
    fun redisCacheConfig(): RedisCacheConfiguration {
        // 解决 kotlin data class 没有无參构造时 反序列化报错
        val objectMapper = ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .setTimeZone(TimeZone.getDefault())
            .activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                    .allowIfBaseType(Any::class.java)
                    .build(), ObjectMapper.DefaultTyping.EVERYTHING
            )
        return RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .entryTtl(30.seconds.toJavaDuration())   // 默认配置， 默认超时时间为30min
            .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper)))
            .computePrefixWith { prefix ->
                if (namespace.isNotEmpty()) {
                    "$namespace:$prefix::"
                } else {
                    "$prefix::"
                }
            }
    }

    @Bean
    fun cacheManager(
        connectionFactory: LettuceConnectionFactory,
        defaultCacheConfig: RedisCacheConfiguration,
        cacheConfigList: List<Topic>,
    ): RedisCacheManager {

        val builder = RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultCacheConfig)

        // 自定义 cacheName 对应 redis cache config
        val cacheConfigMap = cacheConfigList.associate {
            val duration = it.duration ?: Duration.ZERO
            val newConfig = redisCacheConfig().entryTtl(duration.toJavaDuration())
            it.name to newConfig
        }
        builder.withInitialCacheConfigurations(cacheConfigMap)

        // 处理CacheEvict 走scan
        builder.cacheWriter(
            RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory, BatchStrategies.scan(batchSize))
        )

        return builder.build()
    }


}