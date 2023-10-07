package com.realworld.article.application.dto

import com.realworld.user.application.dto.UserDto

data class ArticleResult(
    val articleDto: ArticleDto,
    val authorDto: UserDto,
    val tagList: List<String>,
    val isSelfFollowing: Boolean,
)
