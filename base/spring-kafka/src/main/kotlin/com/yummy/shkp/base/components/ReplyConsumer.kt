package com.yummy.shkp.base.components

import com.yummy.shkp.base.types.Messages
import com.yummy.shkp.base.utils.logger
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class ReplyConsumer(
    private val channelCache: ChannelCache
) : Consumer<Message<*>> {
    private val log = logger()
    override fun accept(t: Message<*>) {
        log.info("reply MessageHandling")
        val correlationId =
            t.headers[Messages.HEADER_CORRELATION_ID]?.toString() ?: throw Exception("correlationId missing")
        runBlocking {
            channelCache.reply(correlationId, t)
        }
    }
}