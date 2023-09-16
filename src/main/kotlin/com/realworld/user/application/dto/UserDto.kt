package com.realworld.user.application.dto

import com.realworld.user.domain.User
import com.realworld.user.presentation.dto.AuthenticationUser
import com.realworld.user.presentation.dto.Profile
import java.time.Instant

data class UserDto(
    val id: Long? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val username: String,
    val email: String,
    val encodedPassword: String,
    val bio: String? = null,
    val image: String? = null,
) {
    companion object {
        fun User.toDto() = UserDto(
            id = this.id,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            username = this.username,
            email = this.email,
            encodedPassword = this.encodedPassword,
            bio = this.bio,
            image = this.image,
        )

        fun UserDto.toEntity() = User(
            id = this.id,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            username = this.username,
            email = this.email,
            encodedPassword = this.encodedPassword,
            bio = this.bio,
            image = this.image,
        )

        fun UserDto.toAuthenticationUser(token: String) = AuthenticationUser(
            email = this.email,
            token = token,
            username = this.username,
            bio = this.bio,
            image = this.image,
        )

        fun UserDto.toProfile(following: Boolean) = Profile(
            username = this.username,
            bio = this.bio,
            image = this.image,
            following = following,
        )
    }
}
