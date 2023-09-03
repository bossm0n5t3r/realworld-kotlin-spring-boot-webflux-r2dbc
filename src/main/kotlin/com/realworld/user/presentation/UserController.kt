package com.realworld.user.presentation

import com.realworld.user.application.UserService
import com.realworld.user.dto.AuthenticationUser
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
    suspend fun signUp(
        @RequestBody signUpRequest: Mono<UserWrapper<SignUpRequest>>,
    ): UserWrapper<AuthenticationUser> {
        return userService.signUp(signUpRequest)
    }
}
