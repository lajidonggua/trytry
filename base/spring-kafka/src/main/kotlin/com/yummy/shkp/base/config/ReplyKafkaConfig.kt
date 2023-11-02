package com.yummy.shkp.base.config

import com.yummy.shkp.base.components.ReplyConsumer
import com.yummy.shkp.base.types.HeaderType
import com.yummy.shkp.base.types.Messages
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import java.util.function.Consumer


@Configuration
class ReplyKafkaConfig {

    @Bean("reply")
    fun reply(
        defaultReplyConsumer: ReplyConsumer
    ): Consumer<Message<*>> {
        return Consumer { message ->
            when (message.headers[Messages.HEADER_TYPE]) {
                HeaderType.RESPONSE -> defaultReplyConsumer.accept(message)
                else -> null
            } ?: message.headers.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment::class.java)?.acknowledge()
        }
    }


}