package com.realworld.security

data class TokenPrincipal(
    val userId: String,
    val token: String,
)
