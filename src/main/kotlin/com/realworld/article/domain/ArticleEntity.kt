package com.realworld.article.domain

import com.realworld.article.application.dto.ArticleDto
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "article_info")
data class ArticleEntity(
    @Id
    val id: Long? = null,
    @CreatedDate
    var createdAt: Instant,
    @LastModifiedDate
    var updatedAt: Instant,
    var slug: String,
    var authorId: Long,
    var title: String,
    var description: String,
    var body: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArticleEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    constructor(dto: ArticleDto) : this(
        id = dto.id,
        createdAt = dto.createdAt,
        updatedAt = dto.updatedAt,
        slug = dto.slug,
        authorId = dto.authorId,
        title = dto.title,
        description = dto.description,
        body = dto.body,
    )

    fun toDto() = ArticleDto(this)
}
