package com.yummy.shkp.config

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.generator.toSchema
import com.yummy.shkp.base.utils.logger
import graphql.Scalars
import graphql.schema.GraphQLType
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ByteArrayResource
import kotlin.reflect.KClass
import kotlin.reflect.KType


interface GraphqlQuery

@Configuration
class GraphqlSchemaGenConfig {

    private val log = logger()

    @Bean
    fun sourceBuilderCustomizer(
        query: GraphqlQuery?
    ) = GraphQlSourceBuilderCustomizer { sourceBuilder ->

        val config = SchemaGeneratorConfig(
            supportedPackages = listOf("com.yummy.shkp.controller"),
            hooks = CustomScalarGeneratorHooks()
        )

        val schema = toSchema(
            config = config,
            queries = query?.let { listOf(TopLevelObject(it)) } ?: emptyList()
        )

        val schemaStr = schema.print()

        log.info("##################### Graphql Schema - Start #####################")
        log.info(schemaStr)
        log.info("##################### Graphql Schema - End #####################")

        sourceBuilder
            .schemaResources(ByteArrayResource(schemaStr.toByteArray()))
    }
}

// Hooks are given to the configuration

class CustomScalarGeneratorHooks : SchemaGeneratorHooks{
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when(type.classifier as? KClass<*>){
        String::class -> Scalars.GraphQLString
        else -> null
    }
}
