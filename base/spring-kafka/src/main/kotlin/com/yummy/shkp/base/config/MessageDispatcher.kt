package com.yummy.shkp.base.config

import com.yummy.shkp.base.types.HeaderType
import com.yummy.shkp.base.types.Messages
import com.yummy.shkp.base.utils.logger
import org.apache.logging.log4j.LogManager
import org.springframework.cloud.function.context.MessageRoutingCallback
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class MessageDispatcher: MessageRoutingCallback {

    private val log = logger()

    override fun routingResult(message: Message<*>?): String {
        log.info("Message received: $message")
        return when (message!!.headers[Messages.HEADER_TYPE].toString()){
            HeaderType.REQUEST.toString()->"Demo"
            HeaderType.RESPONSE.toString()->"ReplyMessageHandler"
            else->throw Exception("Not support Type")
        }
    }

}