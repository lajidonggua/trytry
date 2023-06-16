package com.yummy.shkp.service

import com.yummy.shkp.base.components.MessagePublisher
import com.yummy.shkp.base.utils.logger
import com.yummy.shkp.controller.TestObj
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ExceptionHandler
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Service
@EnableCaching
class TestService(
    private val messagePublisher: MessagePublisher, val testService2: TestService2
) {

    private val log = logger()

    @Cacheable("TestService", key = "#root.methodName")
    suspend fun test(): TestObj {
        log.debug("real call")
        log.info("real call info")
        return TestObj(
            "name${messagePublisher.publish("test-demo", "Test", Int::class)}"
        )
    }

    @Cacheable("TestService", key = "#root.methodName")
    suspend fun testFlux(): Flux<TestObj> {
        log.debug("real call")
        return  testService2.testFlux222().doOnNext { log.debug("testFlux"+it.name) }.delayElements(1.seconds.toJavaDuration())
    }
    @Cacheable("TestService", key = "#root.methodName")
     fun testFlux2(): Flux<TestObj> {
        log.debug("real call")
        return  testService2.testFlux222().map {TestObj("testFlux2"+it.name )}
    }

    @Cacheable("TestService", key = "#root.methodName")
    fun testMono(): Mono<TestObj> {
        log.debug("real call")
        return  Mono.just(TestObj(name = "test"))
    }
}



data class Test(
    val a: String? = "init",
    val b: String
)

@Service
class TestEx {
    fun t() {
        println("ClassNotFoundException throw")
        throw ClassNotFoundException()
    }
}

@Service
class TT {
    fun t() {
        println("ClassNotFoundException throw")
        throw ClassNotFoundException()
    }

    @ExceptionHandler(ClassNotFoundException::class)
    fun handleEx() {
        println("ClassNotFoundException handle")
    }
}

@Service
class TestService2 {

    private val log = logger()
     fun testFlux222(): Flux<TestObj> {
        return Flux.just(TestObj("name"), TestObj("name2")).delayElements(Duration.ofSeconds(1L)).doOnNext { log.debug("testFlux222"+it.name) }
    }
}