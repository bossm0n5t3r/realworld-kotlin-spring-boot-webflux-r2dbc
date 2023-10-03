package com.realworld.security

import com.realworld.exception.ErrorCode
import com.realworld.exception.InvalidRequestException
import com.realworld.user.application.dto.UserDto
import com.realworld.user.application.dto.UserDto.Companion.toDto
import com.realworld.user.domain.UserRepository
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class UserSessionProvider(
    private val userRepository: UserRepository,
) {
    fun getCurrentUserSession(): Mono<UserSession> {
        return ReactiveSecurityContextHolder.getContext()
            .publishOn(Schedulers.boundedElastic())
            .flatMap { context ->
                val tokenPrincipal = context.authentication.principal as TokenPrincipal
                userRepository.findAllById(tokenPrincipal.userId.toLong())
                    .next()
                    .map { UserSession(it.toDto(), tokenPrincipal.token) }
            }
    }

    fun getCurrentUserDtoOrError() = getCurrentUserSession()
        .map { it.userDto }
        .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND)))
}

data class UserSession(
    val userDto: UserDto,
    val token: String,
)
