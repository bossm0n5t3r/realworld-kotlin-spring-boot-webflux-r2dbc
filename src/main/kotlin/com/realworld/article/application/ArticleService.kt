package com.realworld.article.application

import com.realworld.article.application.dto.ArticleResult
import com.realworld.article.application.dto.CreateArticleParameters
import com.realworld.article.application.dto.UpdateArticleParameters
import com.realworld.article.domain.ArticleEntity
import com.realworld.article.domain.ArticleRepository
import com.realworld.article.domain.ArticleTemplateRepository
import com.realworld.article.presentation.dto.Article
import com.realworld.common.domain.OffsetBasedPageable
import com.realworld.exception.ErrorCode
import com.realworld.exception.InvalidRequestException
import com.realworld.meta.application.MetaArticleTagService
import com.realworld.meta.application.MetaFolloweeFollowerService
import com.realworld.meta.application.MetaUserFavoriteArticleService
import com.realworld.security.UserSessionProvider
import com.realworld.tag.application.TagService
import com.realworld.user.application.UserService
import com.realworld.user.application.dto.UserDto
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import java.util.Optional
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
    fun createArticle(request: Mono<CreateArticleParameters>): Mono<ArticleResult> {
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
                            ArticleResult(pair.first, pair.second, createArticleDto.tagList, isSelfFollowing)
                        }
                    }
            }
    }

    fun getArticles(
        tag: String?,
        author: String?,
        favoritedByUser: String?,
        limit: Int,
        offset: Long,
    ): Mono<List<Article>> {
        // FIXME 메서드가 너무 길어서 리팩터링 필요
        return Mono
            .zip(
                userSessionProvider.getCurrentUserSession()
                    .flatMap { getFollowingIdSetOrEmpty(it.userDto.id) }
                    .switchIfEmpty(Mono.just(emptySet())),
                getOptionalUserDtoFromUsername(author),
                getOptionalUserDtoFromUsername(favoritedByUser),
            )
            .flatMap {
                val followingIdSet = it.t1
                val authorUserDto = it.t2.getOrNull()
                val favoritedUserId = it.t3.getOrNull()?.id

                getFilteredArticleIds(tag, favoritedUserId)
                    .flatMap { filteredIds ->
                        articleTemplateRepository.findNewestArticlesFilteredBy(
                            filteredIds = filteredIds,
                            authorId = authorUserDto?.id,
                            limit = limit,
                            offset = offset,
                        )
                            .publishOn(Schedulers.boundedElastic())
                            .flatMap { articleEntity ->
                                articleEntity.toArticle(authorUserDto, followingIdSet)
                            }
                            .collectList()
                    }
            }
    }

    private fun getFollowingIdSetOrEmpty(userId: Long?) =
        metaFolloweeFollowerService.getFollowingIds(userId)
            .collectList()
            .map { it.toSet() }
            .switchIfEmpty(Mono.just(emptySet()))

    private fun getOptionalUserDtoFromUsername(username: String?) =
        userService.getUserDtoFromUsername(username)
            .map { Optional.of(it) }
            .switchIfEmpty(Mono.just(Optional.empty()))

    private fun getFilteredArticleIds(tag: String?, favoritedUserId: Long?): Mono<Set<Long>> {
        return Mono.zip(
            metaUserFavoriteArticleService.getFavoriteArticleIds(favoritedUserId),
            metaArticleTagService.getArticleIdsFromTagName(tag),
        )
            .map {
                val favoriteArticleIds = it.t1
                val articleIdsFromTagName = it.t2
                /**
                 * FIXME
                 *  tag 의 값이 존재하지만, 해당 tag 에 해당하는 articleIds 가 비어있을 때와
                 *  tag 파라미터 자체가 없어서, union 을 해야하는 경우를 분리해야한다.
                 */
                if (favoriteArticleIds.isEmpty() || articleIdsFromTagName.isEmpty()) {
                    it.t1.union(it.t2)
                } else {
                    it.t1.intersect(it.t2)
                }
            }
    }

    private fun ArticleEntity.toArticle(
        authorUserDto: UserDto?,
        followingIdSet: Set<Long>,
    ): Mono<Article> {
        return (
            authorUserDto
                ?.let { Mono.just(Optional.of(it)) }
                ?: userService.getUserDtoFromUserId(this.authorId)
                    .map { Optional.of(it) }
                    .switchIfEmpty(Mono.just(Optional.empty()))
            )
            .map {
                val articleAuthorUserDto = it.getOrNull()
                Optional.ofNullable(
                    articleAuthorUserDto?.toProfile(
                        following = followingIdSet.contains(
                            articleAuthorUserDto.id,
                        ),
                    ),
                )
            }
            .zipWith(metaArticleTagService.getTagsFromArticleId(this.id))
            .map {
                val profile = it.t1.getOrNull()
                val tagList = it.t2
                this.toDto().toArticle(tagList, profile)
            }
    }

    fun feed(limit: Int, offset: Long): Mono<List<Article>> {
        return userSessionProvider.getCurrentUserDtoOrError()
            .flatMap { getFollowingIdSetOrEmpty(it.id) }
            .flatMap { followingIdSet ->
                articleRepository.findAllByAuthorIdIn(
                    followingIdSet,
                    OffsetBasedPageable(limit, offset, Sort.by(Sort.Direction.DESC, ArticleEntity::createdAt.name)),
                )
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap { articleEntity ->
                        articleEntity.toArticle(null, followingIdSet)
                    }
                    .collectList()
            }
    }

    fun getArticle(slug: String): Mono<ArticleResult> {
        return articleRepository.findBySlug(slug)
            .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.ARTICLE_NOT_FOUND)))
            .flatMap { articleEntity ->
                val articleDto = articleEntity.toDto()

                Mono.zip(
                    userService.getUserDtoFromUserId(articleDto.authorId)
                        .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))),
                    metaArticleTagService.getTagsFromArticleId(articleDto.id),
                ).map {
                    val authorDto = it.t1
                    val tagList = it.t2

                    ArticleResult(
                        articleDto = articleDto,
                        authorDto = authorDto,
                        tagList = tagList,
                        isSelfFollowing = false,
                    )
                }
            }
    }

    fun updateArticle(slug: String, updateArticleParameters: Mono<UpdateArticleParameters>): Mono<ArticleResult> {
        return Mono
            .zip(
                updateArticleParameters,
                userSessionProvider.getCurrentUserSession()
                    .mapNotNull { it.userDto }
                    .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))),
                articleRepository.findBySlug(slug)
                    .map { it.toDto() }
                    .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.ARTICLE_NOT_FOUND))),
            )
            .flatMap {
                val updateArticleRequest = it.t1
                val authorDto = it.t2
                val originArticleDto = it.t3

                val updatedArticleDto = originArticleDto.update(updateArticleRequest)

                Mono.zip(
                    articleRepository.save(updatedArticleDto.toEntity())
                        .map { saved -> saved.toDto() },
                    Mono.just(authorDto),
                    metaArticleTagService.getTagsFromArticleId(updatedArticleDto.id),
                    metaFolloweeFollowerService.isFollow(authorDto.id, authorDto.id),
                )
            }
            .map {
                ArticleResult(
                    articleDto = it.t1,
                    authorDto = it.t2,
                    tagList = it.t3,
                    isSelfFollowing = it.t4,
                )
            }
    }

    fun deleteArticle(slug: String): Mono<Unit> = Mono
        .zip(
            userSessionProvider.getCurrentUserSession()
                .map { it.userDto }
                .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))),
            articleRepository.findBySlug(slug)
                .map { it.toDto() }
                .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.ARTICLE_NOT_FOUND))),
        )
        .publishOn(Schedulers.boundedElastic())
        .handle { it, sink ->
            val userDto = it.t1
            val articleDto = it.t2

            if (articleDto.authorId != userDto.id) {
                sink.error(InvalidRequestException(ErrorCode.USER_NOT_MATCHED))
                return@handle
            }

            articleRepository.delete(articleDto.toEntity()).subscribe()
        }
}
