package com.realworld.configuration

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer

@Configuration
class R2dbcConfiguration {
    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        return initializer
    }

    @Bean
    fun r2dbcEntityTemplate(connectionFactory: ConnectionFactory) = R2dbcEntityTemplate(connectionFactory)
}
