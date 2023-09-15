package com.realworld.user.presentation

import com.realworld.user.application.UserService
import com.realworld.user.application.dto.SignInDto.Companion.toSignInDto
import com.realworld.user.application.dto.SignUpDto.Companion.toSignUpDto
import com.realworld.user.application.dto.UpdateUserDto.Companion.toUpdateUserDto
import com.realworld.user.application.dto.UserDto.Companion.toAuthenticationUser
import com.realworld.user.application.dto.UserDto.Companion.toProfile
import com.realworld.user.presentation.dto.AuthenticationUser
import com.realworld.user.presentation.dto.AuthenticationUser.Companion.withUserWrapper
import com.realworld.user.presentation.dto.Profile
import com.realworld.user.presentation.dto.Profile.Companion.withProfileWrapper
import com.realworld.user.presentation.dto.ProfileWrapper
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
        return userService.signUp(signUpRequest.map { it.user.toSignUpDto() })
            .map { (userDto, token) -> userDto.toAuthenticationUser(token).withUserWrapper() }
    }

    @PostMapping("/api/users/login")
    fun signIn(
        @RequestBody signInRequest: Mono<UserWrapper<SignInRequest>>,
    ): Mono<UserWrapper<AuthenticationUser>> {
        return userService.signIn(signInRequest.map { it.user.toSignInDto() })
            .map { (userDto, token) -> userDto.toAuthenticationUser(token).withUserWrapper() }
    }

    @GetMapping("/api/user")
    fun getUser(): Mono<UserWrapper<AuthenticationUser>> {
        return userService.getUser()
            .map { it.userDto.toAuthenticationUser(it.token).withUserWrapper() }
    }

    @PutMapping("/api/user")
    fun updateUser(
        @RequestBody updateRequest: Mono<UserWrapper<UpdateRequest>>,
    ): Mono<UserWrapper<AuthenticationUser>> {
        return userService.update(updateRequest.map { it.user.toUpdateUserDto() })
            .map { (userDto, token) -> userDto.toAuthenticationUser(token).withUserWrapper() }
    }

    @GetMapping("/api/profiles/{username}")
    fun getProfile(@PathVariable username: String): Mono<ProfileWrapper<Profile>> {
        return userService.getProfile(username)
            .map { (userDto, following) -> userDto.toProfile(following).withProfileWrapper() }
    }

    @PostMapping("/api/profiles/{username}/follow")
    fun followUser(@PathVariable username: String): Mono<ProfileWrapper<Profile>> {
        return userService.followUser(username)
            .map { (userDto, following) -> userDto.toProfile(following).withProfileWrapper() }
    }
}
