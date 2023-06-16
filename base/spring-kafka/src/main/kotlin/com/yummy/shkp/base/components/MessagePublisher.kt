package com.yummy.shkp.base.components

import com.fasterxml.jackson.databind.ObjectMapper
import com.yummy.shkp.base.types.HeaderType
import com.yummy.shkp.base.types.Messages
import com.yummy.shkp.base.utils.logger
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.util.MimeTypeUtils
import java.rmi.UnexpectedException
import java.util.*
import kotlin.reflect.KClass


@Component
class MessagePublisher(
    private val streamBridge: StreamBridge,
    private val channelCache: ChannelCache,
    private val objectMapper: ObjectMapper,
) {

    private val log = logger()

    suspend fun <P : Any> publish(
        topic: String,
        input: Any,
        rspType: KClass<P>,
        customHeaders: Map<String, Any> = emptyMap()
    ): P {
        log.info("Publish message to $topic")

        val correlationId = UUID.randomUUID().toString()
        val headers = MessageHeaders(
            customHeaders + mapOf(
                Messages.HEADER_TYPE to HeaderType.REQUEST.toString(),
                Messages.HEADER_SOURCE to "local-yummyTest",
                Messages.HEADER_CORRELATION_ID to correlationId,
                MessageHeaders.CONTENT_TYPE to MimeTypeUtils.APPLICATION_JSON_VALUE,
            )
        )

        val message = MessageBuilder.createMessage(input, headers)
        val successful = streamBridge.send(topic, message)
        if (!successful) {
            throw UnexpectedException("publish failed for unknown reason")
        }
        channelCache.subscribe(correlationId).receive().let { msg ->
            val payload = msg.payload
            return objectMapper.readValue(payload as ByteArray, rspType.java)
        }
    }

    suspend fun reply(inputMsg: Message<*>, rspData: Any) {
        val inputHeaders = inputMsg.headers
        val replyTopic = inputHeaders[Messages.HEADER_SOURCE].toString()
        val correlationId = inputHeaders[Messages.HEADER_CORRELATION_ID].toString()
        log.info("reply message to $replyTopic")
        val headers = MessageHeaders(
            mapOf(
                Messages.HEADER_TYPE to HeaderType.RESPONSE.toString(),
                Messages.HEADER_SOURCE to "test-demo",
                Messages.HEADER_CORRELATION_ID to correlationId,
                MessageHeaders.CONTENT_TYPE to MimeTypeUtils.APPLICATION_JSON_VALUE
            )
        )
        // 这里转化一下，防止objectMapper Read的时候报错
        val rspBytes = objectMapper.writeValueAsBytes(rspData)
        val message = MessageBuilder.createMessage(rspBytes, headers)
        val successful = streamBridge.send(replyTopic, message)
        if (!successful) {
            throw UnexpectedException("reply failed for unknown reason")
        }
    }
}
