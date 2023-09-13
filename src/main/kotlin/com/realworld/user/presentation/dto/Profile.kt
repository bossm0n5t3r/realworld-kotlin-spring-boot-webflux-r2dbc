package com.realworld.user.presentation.dto

data class Profile(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
) {
    companion object {
        fun Profile.withProfileWrapper() = ProfileWrapper(this)
    }
}
