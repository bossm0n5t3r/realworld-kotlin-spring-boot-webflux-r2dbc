package com.realworld.article.presentation.dto

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
    fun withArticleWrapper() = ArticleWrapper(this)
}
