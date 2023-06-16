package com.yummy.shkp.base.types

object Messages {

    const val HEADER_TYPE = "type"
    const val HEADER_SOURCE = "source"
    const val HEADER_CORRELATION_ID = "correlationId"
    const val HEADER_MSG_TYPE = "msgType"
    const val HEADER_STREAM_FINAL = "streamFinal"
    const val HEADER_LOCALE = "locale"
    const val HEADER_PRINCIPAL = "principal"
    const val HEADER_AUTHORITIES = "authorities"

    const val MSG_TYPE_REQUEST = "request"
    const val MSG_TYPE_RESPONSE = "response"
    const val MSG_TYPE_ERROR_RESPONSE = "error_response"

    const val DEFAULT_REPLY_CONSUMER = "DefaultReplyConsumer"
    const val DEFAULT_ERROR_REPLY_CONSUMER = "DefaultErrorReplyConsumer"
    const val DEFAULT_ERROR_DISPATCHER = "ErrorDispatcher"
    const val IGNORE_MESSAGE_CONSUMER = "IgnoreMessageConsumer"
}