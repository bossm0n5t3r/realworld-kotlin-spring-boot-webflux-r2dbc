package com.realworld.user.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table

@Suppress("LongParameterList")
@Table("user_info")
data class User(
    @Id
    val id: Long? = null,
    var username: String,
    var email: String,
    var encodedPassword: String,
    var bio: String? = null,
    var image: String? = null,
    var followingIds: String? = null,
    var favoriteArticlesIds: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    @Transient
    val followingIdList = followingIds?.split(",")?.map { it.toLong() } ?: emptyList()
}
