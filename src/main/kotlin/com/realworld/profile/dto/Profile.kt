package com.realworld.profile.dto

data class Profile(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
)
