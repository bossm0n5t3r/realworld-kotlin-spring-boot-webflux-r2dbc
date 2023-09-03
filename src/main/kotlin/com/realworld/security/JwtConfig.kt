package com.realworld.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.kotlin.core.publisher.toMono

@Configuration
class JwtConfig {
    @Bean
    fun jwtServerAuthenticationConverter(tokenFormatter: TokenFormatter): ServerAuthenticationConverter =
        ServerAuthenticationConverter { exchange ->
            val authorizationHeader = getAuthorizationHeader(exchange)
            authorizationHeader?.let {
                val token = tokenFormatter.getRowToken(it)
                UsernamePasswordAuthenticationToken(token, token)
            }.toMono()
        }

    private fun getAuthorizationHeader(exchange: ServerWebExchange): String? {
        val headers = exchange.request.headers[HttpHeaders.AUTHORIZATION] ?: emptyList()
        return headers.firstOrNull(String::isNotEmpty)
    }

    @Bean
    fun jwtAuthManager(signer: UserTokenProvider): ReactiveAuthenticationManager =
        ReactiveAuthenticationManager { authentication ->
            val token = authentication.credentials as String
            val jws = signer.validate(token)
            val authority = SimpleGrantedAuthority("ROLE_USER")
            val userId = jws.subject
            val tokenPrincipal = TokenPrincipal(userId, token)
            UsernamePasswordAuthenticationToken(tokenPrincipal, token, listOf(authority)).toMono()
        }

    @Bean
    fun authenticationFilter(
        manager: ReactiveAuthenticationManager,
        converter: ServerAuthenticationConverter,
    ): AuthenticationWebFilter = AuthenticationWebFilter(manager).apply {
        setServerAuthenticationConverter(converter)
    }
}
