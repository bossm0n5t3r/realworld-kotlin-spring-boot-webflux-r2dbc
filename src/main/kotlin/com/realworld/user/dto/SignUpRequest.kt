package com.realworld.user.dto

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
)
