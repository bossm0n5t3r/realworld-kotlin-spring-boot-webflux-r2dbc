package com.realworld.user.application

import com.realworld.exception.InvalidRequestException
import com.realworld.security.UserPasswordEncoder
import com.realworld.security.UserTokenProvider
import com.realworld.user.domain.User
import com.realworld.user.domain.UserRepository
import com.realworld.user.dto.AuthenticationUser
import com.realworld.user.dto.SignInRequest
import com.realworld.user.dto.SignUpRequest
import com.realworld.user.dto.UserWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userPasswordEncoder: UserPasswordEncoder,
    private val userTokenProvider: UserTokenProvider,
) {
    private fun Mono<UserWrapper<SignUpRequest>>.isValid(): Mono<UserWrapper<SignUpRequest>> {
        return this
            .publishOn(Schedulers.boundedElastic())
            .handle { request, sink ->
                val signUpRequest = request.user
                var isError = false
                userRepository.existsByEmail(signUpRequest.email).subscribe {
                    if (it) {
                        isError = true
                        sink.error(InvalidRequestException("email", "already exists"))
                        return@subscribe
                    }
                }
                userRepository.existsByUsername(signUpRequest.username).subscribe {
                    if (it) {
                        isError = true
                        sink.error(InvalidRequestException("username", "already exists"))
                        return@subscribe
                    }
                }
                if (isError) return@handle
                sink.next(request)
            }
    }

    @Transactional
    fun signUp(request: Mono<UserWrapper<SignUpRequest>>): Mono<UserWrapper<AuthenticationUser>> {
        return request
            .isValid()
            .doOnError { throw it }
            .map {
                val signUpRequest = it.user
                User(
                    email = signUpRequest.email,
                    username = signUpRequest.username,
                    encodedPassword = userPasswordEncoder.encode(signUpRequest.password),
                )
            }
            .flatMap { userRepository.save(it) }
            .map { user ->
                val token = userTokenProvider.generateToken(user.id?.toString())
                user.toAuthenticationUser(token)
            }
            .map { authenticationUser -> authenticationUser.withUserWrapper() }
    }

    fun signIn(signInRequest: Mono<UserWrapper<SignInRequest>>): Mono<UserWrapper<AuthenticationUser>> {
        return signInRequest
            .publishOn(Schedulers.boundedElastic())
            .handle { request, sink ->
                val email = request.user.email
                val password = request.user.password
                var found = false
                userRepository.findAllByEmail(email)
                    .subscribe {
                        if (userPasswordEncoder.matches(password, it.encodedPassword)) {
                            found = true
                            val token = userTokenProvider.generateToken(it.id?.toString())
                            sink.next(it.toAuthenticationUser(token).withUserWrapper())
                            return@subscribe
                        }
                    }
                if (found) return@handle
                sink.error(InvalidRequestException("User", "not found"))
            }
            .doOnError { throw it }
    }
}
