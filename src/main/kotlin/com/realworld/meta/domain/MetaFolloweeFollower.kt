package com.realworld.meta.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("meta_followee_follower")
data class MetaFolloweeFollower(
    @Id
    val id: Long? = null,
    @CreatedDate
    var createdAt: Instant,
    @LastModifiedDate
    var updatedAt: Instant,
    var followeeUserId: Long,
    var followerUserId: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MetaFolloweeFollower

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
