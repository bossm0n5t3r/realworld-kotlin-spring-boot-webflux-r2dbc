package com.realworld.security

import org.springdoc.core.utils.Constants
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    @Throws(Exception::class)
    fun defaultSecurityWebFilterChain(
        http: ServerHttpSecurity,
        webFilter: AuthenticationWebFilter,
    ): SecurityWebFilterChain {
        return http
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .cors { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .authorizeExchange { exchange ->
                exchange
                    .pathMatchers(HttpMethod.POST, "/api/users", "/api/users/login").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/profiles/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
                    .pathMatchers(
                        "${Constants.SWAGGER_UI_PREFIX}/**",
                        "${Constants.DEFAULT_API_DOCS_URL}/**",
                        Constants.DEFAULT_SWAGGER_UI_PATH,
                        "${Constants.DEFAULT_WEB_JARS_PREFIX_URL}/**",
                    ).permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(webFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}
