package com.realworld.article.application

import com.realworld.article.application.dto.ArticleDto
import com.realworld.article.application.dto.ArticleDto.Companion.toDto
import com.realworld.article.application.dto.CreateArticleDto
import com.realworld.article.domain.ArticleRepository
import com.realworld.exception.ErrorCode
import com.realworld.exception.InvalidRequestException
import com.realworld.meta.application.MetaArticleTagService
import com.realworld.meta.application.MetaFolloweeFollowerService
import com.realworld.security.UserSessionProvider
import com.realworld.tag.application.TagService
import com.realworld.user.application.dto.UserDto
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Service
class ArticleService(
    private val userSessionProvider: UserSessionProvider,
    private val articleRepository: ArticleRepository,
    private val tagService: TagService,
    private val metaFolloweeFollowerService: MetaFolloweeFollowerService,
    private val metaArticleTagService: MetaArticleTagService,
) {
    fun createArticle(request: Mono<CreateArticleDto>): Mono<Triple<ArticleDto, UserDto, Boolean>> {
        return request
            .zipWith(
                userSessionProvider.getCurrentUserSession()
                    .mapNotNull { it.userDto }
                    .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))),
            )
            .flatMap {
                val createArticleDto = it.t1
                val userDto = it.t2
                val authorId = userDto.id
                    ?: return@flatMap Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))

                val tagIds = tagService.createTags(createArticleDto.tagList.toFlux())
                articleRepository.save(createArticleDto.toArticleDto(authorId).toEntity())
                    .map { articleEntity ->
                        val articleId = articleEntity.id
                        if (articleId != null) {
                            metaArticleTagService.saveArticleIdToTagIds(articleId, tagIds)
                        }
                        articleEntity.toDto() to userDto
                    }
                    .flatMap { pair ->
                        metaFolloweeFollowerService.isFollow(authorId, authorId).map { isSelfFollowing ->
                            Triple(pair.first, pair.second, isSelfFollowing)
                        }
                    }
            }
    }
}
