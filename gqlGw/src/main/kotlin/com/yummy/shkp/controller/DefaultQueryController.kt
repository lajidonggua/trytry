package com.yummy.shkp.controller

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.yummy.shkp.base.components.MessagePublisher
import com.yummy.shkp.base.utils.logger
import com.yummy.shkp.config.GraphqlQuery
import com.yummy.shkp.service.TestService
import kotlinx.coroutines.runBlocking
import org.springframework.cache.annotation.EnableCaching
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.http.HttpStatus
import org.springframework.messaging.Message
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.Serializable
import java.util.function.Consumer
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Controller
class DefaultQueryController : GraphqlQuery {
    private val log = logger()
    @QueryMapping
    suspend fun test() = GqlTest()

    val webClient = WebClient.builder().build()

    @QueryMapping
    suspend fun test222222222(): Test22 {
        val mono1Url = "http://223.197.242.188/vcity/getvacancy.aspx"
        val mono2Url = "http://223.197.241.118:811/apm/getvacancy.aspx"
        return Test22(VacancyUrl(mono1Url, mono2Url))
    }

    @SchemaMapping
    suspend fun mono1(test222222222: Test22): Mono<Car>{
        return webClient.get().uri(test222222222.vacancyUrl.mono1Url).retrieve()
            .bodyToMono(String::class.java).delayElement(5.seconds.toJavaDuration()).map {
                log.debug("mono1:{}",it)
                XmlMapper().readValue(it, Car::class.java)
            }
    }

    @SchemaMapping
    suspend fun mono2(test222222222: Test22): Mono<Car>{
        return webClient.get().uri(test222222222.vacancyUrl.mono2Url).retrieve()
            .bodyToMono(String::class.java).delayElement(2.seconds.toJavaDuration()).map {
                log.debug("mono2:{}",it)
                XmlMapper().readValue(it, Car::class.java)
            }
    }
}
class Test22(
    val vacancyUrl: VacancyUrl
){
    lateinit var mono1: Car
    lateinit var mono2: Car
}
data class VacancyUrl(
    val mono1Url: String,
    val mono2Url: String
)
data class Car @JsonCreator constructor(
    @JsonProperty("vacancyinfo")
    val vacancyInfo: VacancyInfo
)

data class VacancyInfo @JsonCreator constructor(
    @JsonProperty("capacity")
    val capacity: Int,
    @JsonProperty("vacancy")
    val vacancy: Int
)

@Controller
@EnableCaching
class TestController(
    private val testService: TestService
) {
    private val log = logger()

    @SchemaMapping
    suspend fun print(gqlTest: GqlTest) = "printTest".also {
        log.info("password: Test")
        log.debug("DEBUG Test")
        log.info(testService.test())
    }

    @SchemaMapping
    suspend fun test(gqlTest: GqlTest): TestObj = testService.test()

    @SchemaMapping
    suspend fun testFlux(gqlTest: GqlTest): Flux<TestObj> {
        return testService.testFlux().map {
            log.debug("testFlux Controller" + it.name)
            it
        }.log()
    }

    @SchemaMapping
    fun testFlux2(gqlTest: GqlTest): Flux<TestObj> {
        log.debug("testFlux2 Start")
        log.debug("testFlux2 do")
        return testService.testFlux2().map {
            log.debug("testFlux2 Controller" + it.name)
            it
        }.log()
    }

    @SchemaMapping
    fun testMono(gqlTest: GqlTest): Mono<TestObj> {
        log.debug("testMono Start")
        log.debug("testMono do")
        return testService.testMono().also {
            log.debug("test suspend redis")
            it.doOnNext { obj -> log.debug(obj) }
        }
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class MyException : RuntimeException()


class GqlTest {
    lateinit var print: String
    lateinit var test: TestObj
    lateinit var testFlux: List<TestObj>
    lateinit var testFlux2: List<TestObj>
    lateinit var testMono: TestObj
}

@Service
class Demo(
    private val messagePublisher: MessagePublisher
) : Consumer<Message<*>> {
    private val log = logger()
    override fun accept(message: Message<*>) {
        log.debug("demo get the message:{}", message)
        runBlocking {
            //messagePublisher.reply(message, TestObj(name = "222"))
            messagePublisher.reply(message, 222)
        }
    }

}


data class TestObj(
    val name: String
) : Serializable