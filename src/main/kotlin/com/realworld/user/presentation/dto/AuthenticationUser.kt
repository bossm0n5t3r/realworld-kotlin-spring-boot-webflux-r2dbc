package com.realworld.user.presentation.dto

data class AuthenticationUser(
    val email: String,
    val token: String,
    val username: String,
    val bio: String? = null,
    val image: String? = null,
)
