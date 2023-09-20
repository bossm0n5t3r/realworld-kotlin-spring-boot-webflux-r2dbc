package com.realworld.article.application.dto

import com.realworld.common.toSlug

data class CreateArticleDto(
    val title: String,
    val description: String,
    val body: String,
    val tagList: List<String> = emptyList(),
) {
    fun toArticleDto(authorId: Long) = ArticleDto(
        slug = this.title.toSlug(),
        authorId = authorId,
        title = this.title,
        description = this.description,
        body = this.body,
    )
}
