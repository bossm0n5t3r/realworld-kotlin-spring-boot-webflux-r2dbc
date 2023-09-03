package com.realworld.user.router

import com.realworld.user.application.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.coRouter

@Configuration
@EnableWebFlux
class UserRouterConfig {
    @Bean
    fun root(userService: UserService) = coRouter {
        "/api/users".nest {
            POST("", userService::signUp)
        }
    }
}
