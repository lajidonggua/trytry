package com.yummy.shkp

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.yummy.shkp.service.TT
import com.yummy.shkp.service.TestEx
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(classes = [GqlGwApplication::class])
class TestExTest {
    @Autowired
    lateinit var testEx: `TestEx`

    @Autowired
    lateinit var tt: TT

    @Test
    fun t() {
        val input = "{\"noOtpPasswordLoginInput {\\n phoneNumber: \\\"+85263755566\\\"\\n password: \\\"P@ssw0rd\\\"\\n platform: MOBILE\\n}\\n\"}"
        println(input.toString())
        val regex = Regex("(?<=(plate: \\\")).*?(?=(\\\"))")
        val replacement = "***"
        val result: String = input.replace(regex, replacement)
        println(result)
        assert(true)
    }

    val webClient = WebClient.builder().build()

    @Test
    fun tt() {
        webClient.get().uri("https://www.1823.gov.hk/common/ical/tc.json").retrieve()
            .bodyToMono(String::class.java).map {
                println("receive:$it")
                jsonToMap(it)
            }.block()
        assert(true)
    }

    fun jsonToMap(jsonString: String) {
        //利用Gson,无Bean类json转Map
        val map = Gson().fromJson<Map<String, Any>>(jsonString, Map::class.java)
        //获取Map中的数据
        val vCalendar = (map["vcalendar"] as MutableList<*>)[0] as Map<*, *>
        val vEvent = vCalendar["vevent"] as MutableList<*>
        for (item in vEvent){
            item as Map<*, *>
            println("${(item["dtstart"] as MutableList<*>)[0]}-${(item["dtend"] as MutableList<*>)[0]} ${item["summary"]}")
        }
    }

    @Test
    fun ttt() {
        val obj = Test1(
            testSub = TestSub(
                ttestSSub = listOf(TestSubb(listOf("sdfsd")))
            )
        )
        val obj1 = Test11(
            testSub = TestSub(
                ttestSSub = listOf(TestSubb(listOf()))
            )
        )
        println(jacksonObjectMapper().writeValueAsString(obj))
        val str = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).writeValueAsString(obj1)
        println(str)
    }
}

data class Week(
    val isWeekday: List<String>
)

enum class WeekType(val value: String) {
    Monday("monday"),
    Tuesday("tuesday"),
    Wednesday("wednesday"),
    Thursday("thursday"),
    Friday("friday"),
    Saturday("saturday"),
    Sunday("sunday");

    fun get(value: String) {

    }
}

open class Test1(
    @JsonProperty("Test1")
    open val testSub: TestSub?
)

data class TestSub(
    @JsonProperty("TestSub222222222")
    val ttestSSub: List<TestSubb>?
)

data class TestSubb(
    @JsonProperty("tTestSSub333333")
    val tWWtttttestSSub: List<String>
)

data class Test11(
    @JsonProperty("Test11")
    override val testSub: TestSub?
) : Test1(testSub = testSub)