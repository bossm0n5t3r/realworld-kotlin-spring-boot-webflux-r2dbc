package com.realworld.meta.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("meta_article_tag")
data class MetaArticleTag(
    @Id
    val id: Long? = null,
    @CreatedDate
    var createdAt: Instant = Instant.now(),
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
    var articleId: Long,
    var tagId: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MetaArticleTag

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
