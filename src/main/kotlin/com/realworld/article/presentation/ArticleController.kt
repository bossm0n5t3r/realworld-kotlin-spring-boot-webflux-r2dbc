package com.realworld.article.presentation

import com.realworld.article.application.ArticleService
import com.realworld.article.application.dto.ArticleResult
import com.realworld.article.presentation.dto.Article
import com.realworld.article.presentation.dto.ArticleWrapper
import com.realworld.article.presentation.dto.ArticlesWrapper
import com.realworld.article.presentation.dto.ArticlesWrapper.Companion.toArticlesWrapper
import com.realworld.article.presentation.dto.CreateArticleRequest
import com.realworld.article.presentation.dto.UpdateArticleRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class ArticleController(
    private val articleService: ArticleService,
) {
    @PostMapping("/api/articles")
    fun createArticle(
        @RequestBody createArticleRequest: Mono<ArticleWrapper<CreateArticleRequest>>,
    ): Mono<ArticleWrapper<Article>> {
        return articleService.createArticle(createArticleRequest.map { it.article.toCreateArticleParameters() })
            .map { it.toArticleWithWrapper() }
    }

    @GetMapping("/api/articles")
    fun getArticles(
        @RequestParam(value = "tag", required = false) tag: String? = null,
        @RequestParam(value = "author", required = false) author: String? = null,
        @RequestParam(value = "favorited", required = false) favoritedByUser: String? = null,
        @RequestParam(value = "limit", defaultValue = "20", required = false) limit: Int = 20,
        @RequestParam(value = "offset", defaultValue = "0", required = false) offset: Long = 0,
    ): Mono<ArticlesWrapper<Article>> {
        return articleService.getArticles(tag, author, favoritedByUser, limit, offset).map { it.toArticlesWrapper() }
    }

    @GetMapping("/api/articles/feed")
    fun feed(
        @RequestParam(value = "limit", defaultValue = "20", required = false) limit: Int = 20,
        @RequestParam(value = "offset", defaultValue = "0", required = false) offset: Long = 0,
    ): Mono<ArticlesWrapper<Article>> = articleService.feed(limit, offset).map { it.toArticlesWrapper() }

    @GetMapping("/api/articles/{slug}")
    fun getArticle(@PathVariable slug: String): Mono<ArticleWrapper<Article>> =
        articleService.getArticle(slug)
            .map { it.toArticleWithWrapper() }

    @PutMapping("/api/articles/{slug}")
    fun updateArticle(
        @PathVariable slug: String,
        @RequestBody updateArticleRequest: Mono<UpdateArticleRequest>,
    ): Mono<ArticleWrapper<Article>> =
        articleService.updateArticle(slug, updateArticleRequest.map { it.toUpdateArticleParameters() })
            .map { it.toArticleWithWrapper() }

    private fun ArticleResult.toArticleWithWrapper(): ArticleWrapper<Article> {
        val (
            articleDto,
            authorDto,
            tagList,
            isSelfFollowing,
        ) = this

        return articleDto.toArticle(
            tagList = tagList,
            profile = authorDto.toProfile(following = isSelfFollowing),
        ).withArticleWrapper()
    }

    @DeleteMapping("/api/articles/{slug}")
    fun deleteArticle(@PathVariable slug: String) = articleService.deleteArticle(slug)

    @PostMapping("/api/articles/{slug}/favorite")
    fun favoriteArticle(
        @PathVariable slug: String,
    ): Mono<ArticleWrapper<Article>> = articleService.favoriteArticle(slug)
        .map { it.toArticleWithWrapper() }

    @DeleteMapping("/api/articles/{slug}/favorite")
    fun unfavoriteArticle(
        @PathVariable slug: String,
    ): Mono<ArticleWrapper<Article>> = articleService.unfavoriteArticle(slug)
        .map { it.toArticleWithWrapper() }
}
