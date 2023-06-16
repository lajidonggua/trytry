//package com.yummy.shkp.controller
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.reactive.asFlow
//import kotlinx.coroutines.reactor.asFlux
//import org.springframework.http.MediaType
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//import reactor.core.publisher.Flux
//import java.time.Duration
//
//@RestController
//@RequestMapping("/test")
//class Test2Controller {
//    private val testService = Test2Service()
//    @GetMapping(path = ["/testFlux"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
//    fun test(): Flux<String>{
//        return testService.testFlux().onEach { log.debug("test Before asFlux:$it") }.asFlux().doOnNext { log.debug("test after asFlux:$it") }.log()
//    }
//}
//class Test2Service{
//    fun testFlux(): Flow<String> {
//        return Flux.just("a","b","c").delayElements(Duration.ofSeconds(1L)).doOnNext { log.debug("testFlux before asFlow:$it") }.asFlow().onEach { log.debug("testFlux after asFlow:$it") }
//    }
//}