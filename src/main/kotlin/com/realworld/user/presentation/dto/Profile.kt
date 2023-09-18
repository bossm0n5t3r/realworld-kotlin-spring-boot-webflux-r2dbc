package com.realworld.user.presentation.dto

data class Profile(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
) {
    fun withProfileWrapper() = ProfileWrapper(this)
}
