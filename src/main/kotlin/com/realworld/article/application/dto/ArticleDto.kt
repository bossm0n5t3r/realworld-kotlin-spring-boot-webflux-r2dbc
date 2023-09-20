package com.realworld.article.application.dto

import com.realworld.article.presentation.dto.Article
import com.realworld.user.presentation.dto.Profile
import java.time.Instant
import com.realworld.article.domain.Article as ArticleEntity

data class ArticleDto(
    val id: Long? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val slug: String,
    val authorId: Long,
    val title: String,
    val description: String,
    val body: String,
) {
    fun toEntity() = ArticleEntity(
        id = this.id,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        slug = this.slug,
        authorId = this.authorId,
        title = this.title,
        description = this.description,
        body = this.body,
    )

    fun toArticle(profile: Profile? = null) = Article(
        slug = this.slug,
        title = this.title,
        description = this.description,
        body = this.body,
        tagList = emptyList(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        favorited = false,
        favoritesCount = 0,
        author = profile ?: Profile(),
    )

    companion object {
        fun ArticleEntity.toDto() = ArticleDto(
            id = this.id,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            slug = this.slug,
            authorId = this.authorId,
            title = this.title,
            description = this.description,
            body = this.body,
        )
    }
}
