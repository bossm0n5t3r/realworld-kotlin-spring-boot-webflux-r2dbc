package com.realworld.user.presentation.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SignInRequest(
    @field:Email
    val email: String,
    @field:NotNull
    @field:NotBlank
    val password: String,
)
