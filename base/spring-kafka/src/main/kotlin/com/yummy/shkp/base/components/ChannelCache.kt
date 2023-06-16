package com.yummy.shkp.base.components

import com.yummy.shkp.base.utils.logger
import kotlinx.coroutines.channels.Channel
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class ChannelCache {
    private val log = logger()
    private val channelCacheMap: MutableMap<String, Channel<Message<*>>> = mutableMapOf()
    fun subscribe(correlationId: String): Channel<Message<*>> {
        val channel = Channel<Message<*>>()
        log.debug("subscribe channel:$correlationId")
        channelCacheMap.putIfAbsent(correlationId, channel)
        return channel
    }

    suspend fun reply(correlationId: String, msgData: Message<*>) {
        log.debug("reply channel:$correlationId")
        val channel = channelCacheMap[correlationId] ?: throw Exception("channel not found")
        channel.send(msgData)
    }
}