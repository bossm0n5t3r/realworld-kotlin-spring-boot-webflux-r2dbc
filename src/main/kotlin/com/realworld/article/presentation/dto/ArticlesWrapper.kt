package com.realworld.article.presentation.dto

data class ArticlesWrapper<T>(
    val articlesCount: Int,
    val articles: List<T>,
) {
    companion object {
        fun List<Article>.toArticlesWrapper() =
            ArticlesWrapper(articlesCount = this.size, articles = this)
    }
}
