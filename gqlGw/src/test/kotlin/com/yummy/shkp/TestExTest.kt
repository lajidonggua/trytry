package com.yummy.shkp

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestExTest {

    @Test
    fun t() {
        val input =
            "{\"noOtpPasswordLoginInput {\\n phoneNumber: \\\"+85263755566\\\"\\n password: \\\"P@ssw0rd\\\"\\n platform: MOBILE\\n}\\n\"}"
        println(input.toString())
        val regex = Regex("(?<=(plate: \\\")).*?(?=(\\\"))")
        val replacement = "***"
        val result: String = input.replace(regex, replacement)
        println(result)
        assert(true)
    }
}