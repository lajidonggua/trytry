package com.yummy.shkp.base.components

import com.yummy.shkp.base.types.HeaderType
import com.yummy.shkp.base.types.Messages.HEADER_TYPE
import com.yummy.shkp.base.utils.logger
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.Message
import org.springframework.messaging.support.ErrorMessage
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.full.starProjectedType

@Component
class ErrorDispatcher(
    private val messagePublisher: MessagePublisher
) : Consumer<ErrorMessage> {
    private val log = logger()
    override fun accept(t: ErrorMessage) {
        t::class.starProjectedType
        t.takeIf { supports(t) }?.let {
            resolve(t)
        } ?: log.error(
            "Unable to handle error message, payload {}, header: {}",
            t.payload.cause?.message,
            t.originalMessage?.headers
        )
    }

    fun supports(message: ErrorMessage?): Boolean =
        message?.originalMessage?.headers?.get(HEADER_TYPE) == HeaderType.REQUEST

    fun resolve(message: ErrorMessage?) {
        message?.let {
            val cause = it.payload.findRootCause()

            val description: String =
                "ref: ${UUID.randomUUID()}"
                    .also { logReference -> log.error(logReference, cause) }
            log.debug("error stacktrace: " + cause.stackTrace?.joinToString(System.lineSeparator()))


            val input = it.originalMessage as Message<*>

            runBlocking {
                messagePublisher.reply(input, description)
            }
            ""
        }
    }

}

fun Throwable.findRootCause(): Throwable {
    var cause = this
    while (cause.cause != null) {
        cause = cause.cause!!
    }
    return cause
}