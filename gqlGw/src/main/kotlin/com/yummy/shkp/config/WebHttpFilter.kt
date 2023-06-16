package com.yummy.shkp.config

import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class WebHttpFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return mono { exchange }
            .flatMap { chain.filter(it).doOnError {
                println("doOnError statusCode ${exchange.response.statusCode}")
            } }.doOnSuccess {
                println("doOnSuccess statusCode ${exchange.response.statusCode}")

            }.doOnCancel {
                println("doOnCancel statusCode ${exchange.response.statusCode}")

            }.doFinally{
                println("doFinally statusCode ${exchange.response.statusCode}")
            }
    }
}