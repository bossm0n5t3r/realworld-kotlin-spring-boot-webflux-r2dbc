package com.realworld.article.application.dto

data class UpdateArticleParameters(
    val title: String? = null,
    val description: String? = null,
    val body: String? = null,
)
