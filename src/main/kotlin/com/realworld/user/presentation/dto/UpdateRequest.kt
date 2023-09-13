package com.realworld.user.presentation.dto

import jakarta.validation.constraints.Email

data class UpdateRequest(
    @field:Email
    val email: String?,
    val username: String?,
    val password: String?,
    val image: String?,
    val bio: String?,
)
