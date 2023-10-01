package com.realworld.article.application

import com.realworld.article.application.dto.ArticleDto
import com.realworld.article.application.dto.CreateArticleDto
import com.realworld.article.domain.ArticleEntity
import com.realworld.article.domain.ArticleRepository
import com.realworld.article.domain.ArticleTemplateRepository
import com.realworld.article.presentation.dto.Article
import com.realworld.exception.ErrorCode
import com.realworld.exception.InvalidRequestException
import com.realworld.meta.application.MetaArticleTagService
import com.realworld.meta.application.MetaFolloweeFollowerService
import com.realworld.meta.application.MetaUserFavoriteArticleService
import com.realworld.security.UserSessionProvider
import com.realworld.tag.application.TagService
import com.realworld.user.application.UserService
import com.realworld.user.application.dto.UserDto
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import kotlin.jvm.optionals.getOrNull

@Service
class ArticleService(
    private val userSessionProvider: UserSessionProvider,
    private val userService: UserService,
    private val articleRepository: ArticleRepository,
    private val tagService: TagService,
    private val articleTemplateRepository: ArticleTemplateRepository,
    private val metaFolloweeFollowerService: MetaFolloweeFollowerService,
    private val metaUserFavoriteArticleService: MetaUserFavoriteArticleService,
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

    fun getArticles(
        tag: String?,
        author: String?,
        favoritedByUser: String?,
        limit: Int,
        offset: Int,
    ): Mono<List<Article>> {
        return Mono
            .zip(
                getFollowingIdSet(),
                Mono.just(userService.getUserDtoFromUsername(author)),
                Mono.just(userService.getUserDtoFromUsername(favoritedByUser)),
            )
            .flatMap {
                val followingIdSet = it.t1
                val authorUserDto = it.t2.getOrNull()
                val favoritedUserId = it.t3.getOrNull()?.id
                val filteredIds = getFilteredArticleIds(tag, favoritedUserId)

                articleTemplateRepository.findNewestArticlesFilteredBy(
                    filteredIds = filteredIds,
                    authorId = authorUserDto?.id,
                    limit = limit,
                    offset = offset.toLong(),
                )
                    .publishOn(Schedulers.boundedElastic())
                    .map { articleEntity -> getArticle(articleEntity, authorUserDto, followingIdSet) }
                    .collectList()
            }
    }

    private fun getFollowingIdSet(): Mono<Set<Long>> = userSessionProvider.getCurrentUserSession()
        .flatMap { metaFolloweeFollowerService.getFollowingIds(it.userDto.id).collectList() }
        .map { it.toSet() }
        .switchIfEmpty(Mono.just(emptySet()))

    private fun getFilteredArticleIds(tag: String?, favoritedUserId: Long?): Set<Long> {
        val favoriteArticleIdSet = metaUserFavoriteArticleService.getFavoriteArticleIds(favoritedUserId)
            .getOrNull()
            ?: emptySet()
        val articleIdSetWithTag = metaArticleTagService.getArticleIdsFromTagName(tag)
            .getOrNull()
            ?: emptySet()
        return favoriteArticleIdSet.intersect(articleIdSetWithTag)
    }

    private fun getArticle(articleEntity: ArticleEntity, authorUserDto: UserDto?, followingIdSet: Set<Long>): Article {
        val articleAuthorUserDto = authorUserDto
            ?: userService.getUserDtoFromUserId(articleEntity.authorId).getOrNull()
        val profile = articleAuthorUserDto
            ?.toProfile(following = followingIdSet.contains(articleAuthorUserDto.id))
        return articleEntity.toDto().toArticle(profile)
    }
}
