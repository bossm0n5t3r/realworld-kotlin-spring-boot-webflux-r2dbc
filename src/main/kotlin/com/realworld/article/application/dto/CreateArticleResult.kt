package com.realworld.article.application.dto

import com.realworld.user.application.dto.UserDto

data class CreateArticleResult(
    val articleDto: ArticleDto,
    val userDto: UserDto,
    val tagList: List<String>,
    val isSelfFollowing: Boolean,
)
