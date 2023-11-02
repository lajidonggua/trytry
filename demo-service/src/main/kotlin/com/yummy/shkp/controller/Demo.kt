package com.yummy.shkp.controller

import com.yummy.shkp.base.components.MessagePublisher
import com.yummy.shkp.base.utils.logger
import com.yummy.shkp.entity.Sender
import com.yummy.shkp.repository.SenderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer

@Service
class Demo(
    private val messagePublisher: MessagePublisher,
    private val senderService: SenderService
) : Consumer<Message<Int>> {
    private val log = logger()
    override fun accept(message: Message<Int>) {
        runBlocking {
            val sender = message.payload
            log.debug("start sleeping with thread ${Thread.currentThread().id} (sender:$sender}")
            delay(500L)
            senderService.save(sender.toString())
            log.debug("start sleeping with thread ${Thread.currentThread().id} (sender:$sender}")
            messagePublisher.reply(message, "output $sender")
        }
    }

}

@Service
class SenderService(
    private val senderRepository: SenderRepository
) {
    @Transactional
    fun save(sender: String) {
        senderRepository.save(
            Sender(
                id = null,
                sender = sender
            )
        )
    }
}