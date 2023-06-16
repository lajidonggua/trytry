package com.yummy.shkp.base.config

import com.yummy.shkp.base.utils.logger
import org.apache.logging.log4j.LogManager
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.Hooks
@Component
class MicroMeterConfig: ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val log = logger()
        log.info("MicroMeterConfig load")
        Hooks.enableAutomaticContextPropagation()
    }
}