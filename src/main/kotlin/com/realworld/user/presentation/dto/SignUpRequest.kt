package com.realworld.user.presentation.dto

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
)
