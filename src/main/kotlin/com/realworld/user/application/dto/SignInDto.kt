package com.realworld.user.application.dto

import com.realworld.user.presentation.dto.SignInRequest

data class SignInDto(
    val email: String,
    val password: String,
) {
    companion object {
        fun SignInRequest.toSignInDto() = SignInDto(
            email = email,
            password = password,
        )
    }
}
