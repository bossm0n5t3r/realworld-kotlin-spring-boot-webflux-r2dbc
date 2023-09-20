package com.realworld.article.presentation.dto

import com.realworld.article.application.dto.CreateArticleDto
import jakarta.validation.constraints.NotBlank

data class CreateArticleRequest(
    @field:NotBlank
    val title: String,
    val description: String,
    @field:NotBlank
    val body: String,
    val tagList: List<String> = emptyList(),
) {
    fun toCreateArticleDto() = CreateArticleDto(
        title = this.title,
        description = this.description,
        body = this.body,
        tagList = this.tagList,
    )
}
