package com.realworld.user.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Suppress("LongParameterList")
@Table("user_info")
data class User(
    @Id
    val id: Long? = null,
    @CreatedDate
    var createdAt: Instant,
    @LastModifiedDate
    var updatedAt: Instant,
    var username: String,
    var email: String,
    var encodedPassword: String,
    var bio: String? = null,
    var image: String? = null,
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
}
