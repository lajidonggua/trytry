package com.yummy.shkp.base.config

import com.yummy.shkp.base.config.LogMaskingConverter.Companion.CONVERTER_KEY
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.pattern.ConverterKeys
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter

@Plugin(name = "LogMaskingConverter", category = "Converter")
@ConverterKeys(CONVERTER_KEY)
class LogMaskingConverter(name: String, style: String) : LogEventPatternConverter(name, style) {

    companion object {

        val BLACKLIST = listOf("password")
        const val CONVERTER_KEY = "sensitive"
        const val MASKED_MESSAGE =
            "*********** This log message is masked because it contains sensitive information ***********"

        @JvmStatic
        fun newInstance(config: Configuration, options: Array<String>): LogMaskingConverter {
            return LogMaskingConverter(CONVERTER_KEY, Thread.currentThread().name)
        }
    }

    override fun format(event: LogEvent, logMsgBuilder: StringBuilder) {
        val originalMessage = event.message.formattedMessage
        val maskedMessage = if (BLACKLIST.any { originalMessage.contains(it, ignoreCase = true) }) {
            MASKED_MESSAGE
        } else {
            originalMessage
        }
        logMsgBuilder.append(maskedMessage)
    }
}