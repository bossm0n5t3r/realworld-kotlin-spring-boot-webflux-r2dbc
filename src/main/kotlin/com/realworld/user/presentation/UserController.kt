package com.realworld.user.presentation

import com.realworld.profile.dto.Profile
import com.realworld.profile.dto.ProfileWrapper
import com.realworld.user.application.UserService
import com.realworld.user.presentation.dto.AuthenticationUser
import com.realworld.user.presentation.dto.SignInRequest
import com.realworld.user.presentation.dto.SignUpRequest
import com.realworld.user.presentation.dto.UpdateRequest
import com.realworld.user.presentation.dto.UserWrapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @GetMapping("/api/user")
    fun getUser(): Mono<UserWrapper<AuthenticationUser>> {
        // FIXME Handle error response when throw InvalidJwtException
        return userService.getUser()
    }

    @PutMapping("/api/user")
    fun updateUser(
        @RequestBody updateRequest: Mono<UserWrapper<UpdateRequest>>,
    ): Mono<UserWrapper<AuthenticationUser>> {
        // FIXME Handle error response when throw InvalidJwtException
        return userService.update(updateRequest)
    }

    @GetMapping("/api/profiles/{username}")
    fun getProfile(@PathVariable username: String): Mono<ProfileWrapper<Profile>> {
        // FIXME Handle error response when throw InvalidJwtException
        return userService.getProfile(username)
    }
}
