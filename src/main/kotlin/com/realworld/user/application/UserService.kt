package com.realworld.user.application

import com.realworld.exception.InvalidRequestException
import com.realworld.security.UserPasswordEncoder
import com.realworld.security.UserTokenProvider
import com.realworld.user.domain.User
import com.realworld.user.domain.UserRepository
import com.realworld.user.dto.AuthenticationUser
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
                AuthenticationUser(
                    email = user.email,
                    token = userTokenProvider.generateToken(user.id?.toString()),
                    username = user.username,
                    bio = user.bio,
                    image = user.image,
                )
            }
            .map { authenticationUser -> UserWrapper(authenticationUser) }
    }
}
