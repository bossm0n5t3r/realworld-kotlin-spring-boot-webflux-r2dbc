package com.realworld.article.presentation.dto

data class ArticlesWrapper<T>(
    val articlesCount: Int,
    val articles: List<T>,
)
