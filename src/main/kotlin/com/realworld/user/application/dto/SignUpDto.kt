package com.realworld.user.application.dto

import com.realworld.user.presentation.dto.SignUpRequest

data class SignUpDto(
    val username: String,
    val email: String,
    val password: String,
) {
    companion object {
        fun SignUpRequest.toSignUpDto() = SignUpDto(
            username = this.username,
            email = this.email,
            password = this.password,
        )

        fun SignUpDto.toUserDto(encodedPassword: String) = UserDto(
            username = this.username,
            email = this.email,
            encodedPassword = encodedPassword,
        )
    }
}
