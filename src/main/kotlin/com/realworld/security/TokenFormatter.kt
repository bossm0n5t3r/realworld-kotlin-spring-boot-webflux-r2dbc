package com.realworld.security

import com.realworld.common.Constants.TOKEN_PREFIX
import com.realworld.exception.ErrorCode
import com.realworld.exception.InvalidRequestException
import org.springframework.stereotype.Component

@Component
class TokenFormatter {
    fun getRowToken(authorizationHeader: String): String {
        if (!authorizationHeader.startsWith(TOKEN_PREFIX)) {
            throw InvalidRequestException(ErrorCode.AUTHORIZATION_HEADER_HAS_NO_TOKEN_PREFIX)
        }
        val tokenStarts = TOKEN_PREFIX.length
        return authorizationHeader.substring(tokenStarts)
    }
}
