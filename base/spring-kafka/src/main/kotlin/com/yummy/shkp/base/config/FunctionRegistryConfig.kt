package com.yummy.shkp.base.config

import com.yummy.shkp.base.utils.logger
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.cloud.function.context.FunctionRegistration
import org.springframework.cloud.function.context.FunctionRegistry
import org.springframework.cloud.function.context.catalog.FunctionTypeUtils
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class FunctionRegistryConfig(
    private val functionRegistry: FunctionRegistry,
) : SmartInitializingSingleton, BeanFactoryAware {

    private val log = logger()

    override fun afterSingletonsInstantiated() {
        registerDynamicFunction()
    }

    lateinit var beanFactory: DefaultListableBeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory as DefaultListableBeanFactory
    }

    private fun registerDynamicFunction() {
        log.info("Function Registration start")
        beanFactory.getBeansOfType(Consumer::class.java).values.forEach { consumer ->
            log.info("${consumer::class.simpleName} registering")
            functionRegistry.register(FunctionRegistration(consumer).name(consumer::class.simpleName).type(
                FunctionTypeUtils.discoverFunctionTypeFromClass(consumer::class.java)))
        }
    }

}