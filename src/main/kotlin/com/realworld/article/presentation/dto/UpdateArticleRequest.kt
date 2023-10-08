package com.realworld.article.presentation.dto

import com.realworld.article.application.dto.UpdateArticleParameters

data class UpdateArticleRequest(
    val title: String? = null,
    val description: String? = null,
    val body: String? = null,
) {
    fun toUpdateArticleParameters() = UpdateArticleParameters(
        title = title,
        description = description,
        body = body,
    )
}
