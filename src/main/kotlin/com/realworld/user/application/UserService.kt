package com.realworld.user.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.realworld.exception.InvalidRequestException
import com.realworld.security.UserPasswordEncoder
import com.realworld.security.UserTokenProvider
import com.realworld.user.domain.User
import com.realworld.user.domain.UserRepository
import com.realworld.user.dto.AuthenticationUser
import com.realworld.user.dto.SignUpRequest
import com.realworld.user.dto.UserWrapper
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userPasswordEncoder: UserPasswordEncoder,
    private val userTokenProvider: UserTokenProvider,
) {
    private suspend fun SignUpRequest.isValid(): Either<InvalidRequestException, SignUpRequest> {
        val signUpRequest = this
        return either {
            ensure(userRepository.existsByEmail(signUpRequest.email).awaitSingle().not()) {
                InvalidRequestException("email", "already exists")
            }
            ensure(userRepository.existsByUsername(signUpRequest.username).awaitSingle().not()) {
                InvalidRequestException("username", "already exists")
            }
            signUpRequest
        }
    }

    private fun SignUpRequest.toEntity() = User(
        email = this.email,
        username = this.username,
        encodedPassword = userPasswordEncoder.encode(this.password),
    )

    private fun User.toAuthenticationUser() = AuthenticationUser(
        email = this.email,
        token = userTokenProvider.generateToken(this.id?.toString()),
        username = this.username,
        bio = this.bio,
        image = this.image,
    )

    private suspend fun AuthenticationUser.wrap(): ServerResponse {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(UserWrapper(this))
    }

    private suspend fun User.save(): User {
        return userRepository.save(this).awaitSingle()
    }

    @Transactional
    suspend fun signUp(request: ServerRequest): ServerResponse {
        return request.awaitBody<UserWrapper<SignUpRequest>>()
            .user
            .isValid()
            .fold(
                ifLeft = { throw it },
                ifRight = {
                    it.toEntity()
                        .save()
                        .toAuthenticationUser()
                        .wrap()
                },
            )
    }
}
