package com.yummy.shkp.config

/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.aopalliance.intercept.MethodInvocation
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.cache.Cache
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.interceptor.CacheAspectSupport.*
import org.springframework.cache.interceptor.CacheInterceptor
import org.springframework.cache.interceptor.CacheOperationInvoker
import org.springframework.cache.interceptor.CacheOperationSource
import org.springframework.cache.interceptor.SimpleKeyGenerator
import org.springframework.cache.support.SimpleValueWrapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role
import org.springframework.util.ObjectUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.reflect.Method
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
internal class CoroutineCacheConfiguration {
    @Bean
    fun cachingPostProcessor(): BeanDefinitionRegistryPostProcessor = CoroutineCacheBeanFactoryPostProcessor()

    @Bean(COROUTINE_CACHE_INTERCEPTOR_BEAN)
    fun coroutineCacheInterceptor(
        config: Optional<CachingConfigurer>, cacheOperationSource: CacheOperationSource
    ): CacheInterceptor = CoroutineCacheInterceptor().apply {
        setCacheOperationSources(cacheOperationSource)
        config.ifPresent { cfg ->
            cfg.cacheManager()?.let { setCacheManager(it) }
            cfg.cacheResolver()?.let { cacheResolver = it }
            cfg.keyGenerator()?.let { keyGenerator = it }
            cfg.errorHandler()?.let { errorHandler = it }
        }


        if (keyGenerator is SimpleKeyGenerator) {
            keyGenerator = CoroutineAwareSimpleKeyGenerator()
        }
    }
}

private class CoroutineCacheBeanFactoryPostProcessor : BeanDefinitionRegistryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {}

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        if (registry.containsBeanDefinition(CACHE_INTERCEPTOR_BEAN)) {
            registry.removeBeanDefinition(CACHE_INTERCEPTOR_BEAN)
            registry.registerAlias(COROUTINE_CACHE_INTERCEPTOR_BEAN, CACHE_INTERCEPTOR_BEAN)
        } else {
            registry.removeBeanDefinition(COROUTINE_CACHE_INTERCEPTOR_BEAN)
        }
    }
}

private const val CACHE_INTERCEPTOR_BEAN = "cacheInterceptor"
private const val COROUTINE_CACHE_INTERCEPTOR_BEAN = "coroutineCacheInterceptor"

private class CoroutineCacheInterceptor : CacheInterceptor() {
    override fun invoke(invocation: MethodInvocation): Any? = if (invocation.method.isSuspend) {
        invokeCoroutine(invocation)
    } else {
        super.invoke(invocation)
    }

    @Suppress("UNCHECKED_CAST")
    private fun invokeCoroutine(invocation: MethodInvocation): Any? = try {
        val args = invocation.arguments
        val target = invocation.`this`!!
        val method = invocation.method

        val cachingContinuation = CachingContinuation(args.last() as Continuation<Any?>) { it ->
            execute({ it }, target, method, args).withLazyCacheHandle()
        }
        executeCoroutine(cachingContinuation, invocation, target, method, args)
    } catch (th: CacheOperationInvoker.ThrowableWrapper) {
        throw th.original
    }

    private fun executeCoroutine(
        cachingContinuation: CachingContinuation,
        invocation: MethodInvocation,
        target: Any,
        method: Method,
        args: Array<Any>
    ): Any? {

        val originalContinuation = args.last()
        args.setLast(cachingContinuation)

        return try {
            execute({
                try {
                    invocation.proceed()
                } catch (ex: Throwable) {
                    throw CacheOperationInvoker.ThrowableWrapper(ex)
                }
            }, target, method, args)
        } catch (e: CoroutineSuspendedException) {
            COROUTINE_SUSPENDED
        } finally {
            args.setLast(originalContinuation)
        }
    }

    override fun invokeOperation(invoker: CacheOperationInvoker): Any? {
        val result = super.invokeOperation(invoker)
        return if (result === COROUTINE_SUSPENDED) {
            throw CoroutineSuspendedException()
        } else {
            result
        }
    }

    // Map 储存 result 对应的Cache 和 Key, 便于在lazy Cache Put 使用
    // 用result 的 内存地址来当key, 在多线程的时候也能根据result 取回对应的Cache 和 key
    private val lazyCacheMap = HashMap<Int, Pair<Cache, Any>>()
    private fun Any?.withLazyCacheHandle(): Any? {
        val result = this
        val cacheValue = ObjectUtils.unwrapOptional(result)
        // 获取需要延迟处理的cache
        val (resumeCache, resumeKey) = cacheValue?.let { lazyCacheMap[System.identityHashCode(cacheValue)] }
            ?: return result
        // 及时清除Map,避免不断积累导致oom
        lazyCacheMapRemove(cacheValue)
        return when (cacheValue) {
            is Flux<*> -> {
                val fluxCacheData = mutableListOf<Any>()
                cacheValue.doOnNext { fluxCacheData.add(it) }.doOnComplete {
                    super.doPut(
                        resumeCache,
                        resumeKey,
                        ReactiveRedisResult(type = DataType.Flux, fluxCacheData)
                    )
                }
            }

            is Mono<*> -> {
                cacheValue.doOnNext {
                    super.doPut(resumeCache, resumeKey, ReactiveRedisResult(type = DataType.Mono, it))
                }
            }

            else -> result
        }
    }

    private fun lazyCacheMapPut(result: Any, cacheInfo: Pair<Cache, Any>) {
        lazyCacheMap[System.identityHashCode(result)] = cacheInfo
    }

    private fun lazyCacheMapRemove(result: Any) {
        lazyCacheMap.remove(System.identityHashCode(result))
    }

    override fun doPut(cache: Cache, key: Any, result: Any?) {
        // 延迟cache
        if (result is Flux<*> || result is Mono<*>) {
            lazyCacheMapPut(result, cache to key)
            return
        }
        super.doPut(cache, key, result)
    }

    override fun doGet(cache: Cache, key: Any): Cache.ValueWrapper? {
        val objWrapper = super.doGet(cache, key)
        return if (objWrapper?.get() is ReactiveRedisResult) {
            val obj = objWrapper.get() as ReactiveRedisResult
            val originObj = when (obj.type) {
                DataType.Flux -> Flux.fromIterable(obj.data as Collection<*>)
                DataType.Mono -> Mono.just(obj.data)
                DataType.Object -> obj.data
            }
            return SimpleValueWrapper(originObj)
        } else {
            objWrapper
        }
    }
}

private class CachingContinuation(
    private val delegate: Continuation<Any?>, private val onResume: (Any?) -> Any?
) : Continuation<Any?> {
    override val context = delegate.context

    override fun resumeWith(result: Result<Any?>) {
        if (result.isFailure) {
            delegate.resumeWith(result)
            return
        }
        var resumed = false
        var new: Any? = null
        try {
            new = onResume(result.getOrNull())
        } catch (ex: Throwable) {
            resumed = true
            delegate.resumeWithException(ex)
        }
        if (!resumed) {
            delegate.resume(new)
        }
    }

}

private class CoroutineSuspendedException : RuntimeException()

private class CoroutineAwareSimpleKeyGenerator : SimpleKeyGenerator() {
    override fun generate(target: Any, method: Method, vararg params: Any): Any = when {
        method.isSuspend -> super.generate(target, method, *params.removeLastValue())
        else -> super.generate(target, method, *params)
    }
}

private data class ReactiveRedisResult(
    val type: DataType,
    val data: Any
)

private enum class DataType {
    Mono, Flux, Object
}