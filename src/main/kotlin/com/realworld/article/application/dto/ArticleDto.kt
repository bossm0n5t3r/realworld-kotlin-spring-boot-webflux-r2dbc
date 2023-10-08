package com.realworld.article.application.dto

import com.realworld.article.domain.ArticleEntity
import com.realworld.article.presentation.dto.Article
import com.realworld.common.toSlug
import com.realworld.user.presentation.dto.Profile
import java.time.Instant

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
    constructor(entity: ArticleEntity) : this(
        id = entity.id,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        slug = entity.slug,
        authorId = entity.authorId,
        title = entity.title,
        description = entity.description,
        body = entity.body,
    )

    fun toEntity() = ArticleEntity(this)
    fun toArticle(tagList: List<String>? = null, profile: Profile? = null) =
        Article(this, tagList, profile)

    fun update(updateArticleParameters: UpdateArticleParameters): ArticleDto {
        if (
            updateArticleParameters.title == null &&
            updateArticleParameters.description == null &&
            updateArticleParameters.body == null
        ) {
            return this
        }

        return ArticleDto(
            id = this.id,
            createdAt = this.createdAt,
            updatedAt = Instant.now(),
            slug = updateArticleParameters.title?.toSlug() ?: this.slug,
            authorId = this.authorId,
            title = updateArticleParameters.title ?: this.title,
            description = updateArticleParameters.description ?: this.description,
            body = updateArticleParameters.body ?: this.body,
        )
    }
}
