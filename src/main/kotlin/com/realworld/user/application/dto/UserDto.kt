package com.realworld.user.application.dto

import com.realworld.user.domain.User
import com.realworld.user.presentation.dto.AuthenticationUser
import com.realworld.user.presentation.dto.Profile

data class UserDto(
    val id: Long? = null,
    val username: String,
    val email: String,
    val encodedPassword: String,
    val bio: String? = null,
    val image: String? = null,
    val followingIdList: MutableSet<Long> = mutableSetOf(),
    val favoriteArticlesIdList: MutableSet<Long> = mutableSetOf(),
) {
    companion object {
        fun User.toDto() = UserDto(
            id = this.id,
            username = this.username,
            email = this.email,
            encodedPassword = this.encodedPassword,
            bio = this.bio,
            image = this.image,
            followingIdList = this.followingIds
                ?.takeIf { it.isNotBlank() }
                ?.split(",")
                ?.map { it.toLong() }
                ?.toMutableSet()
                ?: mutableSetOf(),
            favoriteArticlesIdList = this.favoriteArticlesIds
                ?.takeIf { it.isNotBlank() }
                ?.split(",")
                ?.map { it.toLong() }
                ?.toMutableSet()
                ?: mutableSetOf(),
        )

        fun UserDto.toEntity() = User(
            id = this.id,
            username = this.username,
            email = this.email,
            encodedPassword = this.encodedPassword,
            bio = this.bio,
            image = this.image,
            followingIds = this.followingIdList.joinToString(","),
            favoriteArticlesIds = this.favoriteArticlesIdList.joinToString(","),
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
