package com.realworld.security

import com.realworld.exception.InvalidRequestException
import org.jose4j.jwt.consumer.InvalidJwtException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
class JwtConfig {
    @Bean
    fun jwtServerAuthenticationConverter(tokenFormatter: TokenFormatter): ServerAuthenticationConverter =
        ServerAuthenticationConverter { exchange ->
            Mono.justOrEmpty(getAuthorizationHeader(exchange))
                .map {
                    val token = tokenFormatter.getRowToken(it)
                    UsernamePasswordAuthenticationToken(token, token)
                }
        }

    private fun getAuthorizationHeader(exchange: ServerWebExchange): String? {
        val headers = exchange.request.headers[HttpHeaders.AUTHORIZATION] ?: emptyList()
        return headers.firstOrNull { it.isNotEmpty() }
    }

    @Bean
    fun jwtAuthManager(signer: UserTokenProvider): ReactiveAuthenticationManager =
        ReactiveAuthenticationManager { authentication ->
            Mono.justOrEmpty(authentication)
                .map { it.credentials as String }
                .handle { token, sink ->
                    try {
                        val jws = signer.validate(token)
                        val authority = SimpleGrantedAuthority("ROLE_USER")
                        val userId = jws.subject
                        val tokenPrincipal = TokenPrincipal(userId, token)
                        sink.next(UsernamePasswordAuthenticationToken(tokenPrincipal, token, listOf(authority)))
                    } catch (e: InvalidJwtException) {
                        sink.error(InvalidRequestException("Token", "invalid"))
                    }
                }
                .doOnError { throw it }
                .map { it as Authentication }
        }

    @Bean
    fun authenticationFilter(
        manager: ReactiveAuthenticationManager,
        converter: ServerAuthenticationConverter,
    ): AuthenticationWebFilter = AuthenticationWebFilter(manager).apply {
        setServerAuthenticationConverter(converter)
    }
}
