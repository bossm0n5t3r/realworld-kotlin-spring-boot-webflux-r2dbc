package com.realworld.article.presentation.dto

import com.realworld.article.application.dto.ArticleDto
import com.realworld.user.presentation.dto.Profile
import java.time.Instant

data class Article(
    val slug: String,
    val title: String,
    val description: String,
    val body: String,
    val tagList: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val favorited: Boolean,
    val favoritesCount: Int,
    val author: Profile,
) {
    constructor(dto: ArticleDto, tagList: List<String>? = null, profile: Profile? = null) : this(
        slug = dto.slug,
        title = dto.title,
        description = dto.description,
        body = dto.body,
        tagList = tagList ?: emptyList(),
        createdAt = dto.createdAt,
        updatedAt = dto.updatedAt,
        favorited = false,
        favoritesCount = 0,
        author = profile ?: Profile(),
    )

    fun withArticleWrapper() = ArticleWrapper(this)
}
