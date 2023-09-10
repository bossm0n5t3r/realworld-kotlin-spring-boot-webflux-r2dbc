package com.realworld.user.presentation

import com.realworld.user.application.UserService
import com.realworld.user.dto.AuthenticationUser
import com.realworld.user.dto.SignInRequest
import com.realworld.user.dto.SignUpRequest
import com.realworld.user.dto.UserWrapper
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/api/users")
    fun signUp(
        @RequestBody signUpRequest: Mono<UserWrapper<SignUpRequest>>,
    ): Mono<UserWrapper<AuthenticationUser>> {
        return userService.signUp(signUpRequest)
    }

    @PostMapping("/api/users/login")
    fun signIn(
        @RequestBody signInRequest: Mono<UserWrapper<SignInRequest>>,
    ): Mono<UserWrapper<AuthenticationUser>> {
        return userService.signIn(signInRequest)
    }
}
