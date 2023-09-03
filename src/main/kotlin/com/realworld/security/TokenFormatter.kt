package com.realworld.security

import com.realworld.common.Constants.TOKEN_PREFIX
import com.realworld.exception.InvalidRequestException
import org.springframework.stereotype.Component

@Component
class TokenFormatter {
    fun getRowToken(authorizationHeader: String): String {
        if (!authorizationHeader.startsWith(TOKEN_PREFIX)) {
            throw InvalidRequestException("Authorization Header", "has no `Token` prefix")
        }
        val tokenStarts = TOKEN_PREFIX.length
        return authorizationHeader.substring(tokenStarts)
    }
}
