package com.yummy.shkp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class GqlGwApplication

fun main(args: Array<String>) {
    SpringApplication.run(GqlGwApplication::class.java, *args)
}

