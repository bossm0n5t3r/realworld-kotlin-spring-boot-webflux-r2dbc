package com.realworld.user.presentation.dto

data class Profile(
    val username: String = "",
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean = false,
) {
    fun withProfileWrapper() = ProfileWrapper(this)
}
