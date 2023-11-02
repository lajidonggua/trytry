package com.yummy.shkp.controller
import com.yummy.shkp.base.components.MessagePublisher
import com.yummy.shkp.base.const.MessageTopic
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class Test2Controller(
    private val messagePublisher: MessagePublisher
) {

    @GetMapping("/testSleep")
    suspend fun testSleep(@RequestParam sender: Int): String {
        return messagePublisher.publish(MessageTopic.DEMO_SVC,sender)
    }
}