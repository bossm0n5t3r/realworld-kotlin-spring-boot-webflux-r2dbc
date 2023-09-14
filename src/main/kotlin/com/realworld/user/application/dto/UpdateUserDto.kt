package com.realworld.user.application.dto

import com.realworld.user.presentation.dto.UpdateRequest

data class UpdateUserDto(
    val email: String?,
    val username: String?,
    val password: String?,
    val image: String?,
    val bio: String?,
) {
    companion object {
        fun UpdateRequest.toUpdateUserDto() = UpdateUserDto(
            email = email,
            username = username,
            password = password,
            image = image,
            bio = bio,
        )
    }
}
