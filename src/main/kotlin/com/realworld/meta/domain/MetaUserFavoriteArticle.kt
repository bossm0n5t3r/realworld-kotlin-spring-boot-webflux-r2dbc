package com.realworld.meta.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("meta_user_favorite_article")
data class MetaUserFavoriteArticle(
    @Id
    val id: Long? = null,
    @CreatedDate
    var createdAt: Instant,
    @LastModifiedDate
    var updatedAt: Instant,
    var userId: Long,
    var favoriteArticleId: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MetaUserFavoriteArticle

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
