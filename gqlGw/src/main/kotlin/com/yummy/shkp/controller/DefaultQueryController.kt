package com.yummy.shkp.controller

import com.yummy.shkp.base.components.MessagePublisher
import com.yummy.shkp.base.utils.logger
import com.yummy.shkp.config.GraphqlQuery
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class DefaultQueryController(
    private val messagePublisher: MessagePublisher
) : GraphqlQuery {
    private val log = logger()

    @QueryMapping
    fun test() = "test"
}
