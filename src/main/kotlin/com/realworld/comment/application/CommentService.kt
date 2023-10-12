package com.realworld.comment.application

import com.realworld.article.domain.ArticleRepository
import com.realworld.comment.application.dto.CommentDto
import com.realworld.comment.application.dto.CommentResult
import com.realworld.comment.domain.CommentRepository
import com.realworld.comment.presentation.dto.Comment
import com.realworld.exception.ErrorCode
import com.realworld.exception.InvalidRequestException
import com.realworld.meta.application.MetaFolloweeFollowerService
import com.realworld.security.UserSessionProvider
import com.realworld.user.application.UserService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@Service
class CommentService(
    private val userSessionProvider: UserSessionProvider,
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val metaFolloweeFollowerService: MetaFolloweeFollowerService,
    private val userService: UserService,
) {
    fun createComment(slug: String, commentBody: Mono<String>): Mono<CommentResult> {
        return Mono
            .zip(
                userSessionProvider.getCurrentUserSession()
                    .map { it.userDto }
                    .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))),
                articleRepository.findBySlug(slug)
                    .map { it.toDto() }
                    .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.ARTICLE_NOT_FOUND))),
                commentBody,
            )
            .handle { it, sink ->
                val authorDto = it.t1
                val authorId = authorDto.id
                    ?: return@handle sink.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))
                val articleId = it.t2.id
                    ?: return@handle sink.error(InvalidRequestException(ErrorCode.ARTICLE_NOT_FOUND))

                sink.next(
                    Triple(
                        authorDto,
                        authorId,
                        CommentDto(
                            authorId = authorId,
                            articleId = articleId,
                            body = it.t3,
                        ),
                    ),
                )
            }
            .flatMap { (authorDto, authorId, commentDto) ->
                val savedCommentDto = commentRepository.save(commentDto.toEntity())
                    .map { it.toDto() }
                val isSelfFollowing = metaFolloweeFollowerService.isFollow(authorId, authorId)

                Mono
                    .zip(
                        savedCommentDto,
                        isSelfFollowing,
                    )
                    .map {
                        CommentResult(
                            commentDto = it.t1,
                            authorDto = authorDto,
                            isSelfFollowing = it.t2,
                        )
                    }
            }
    }

    fun getComments(slug: String): Mono<List<Comment>> {
        return Mono
            .zip(
                userSessionProvider.getCurrentUserSession()
                    .flatMap { getFollowingIdSetOrEmpty(it.userDto.id) }
                    .switchIfEmpty(Mono.just(emptySet())),
                articleRepository.findBySlug(slug)
                    .map { it.toDto() }
                    .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.ARTICLE_NOT_FOUND))),
            )
            .handle { it, sink ->
                val followingIdSet = it.t1
                val articleId = it.t2.id
                    ?: return@handle sink.error(InvalidRequestException(ErrorCode.ARTICLE_NOT_FOUND))

                sink.next(
                    commentRepository.findAllByArticleId(articleId)
                        .map { entity -> entity.toDto() }
                        .flatMap { commentDto ->
                            val commentAuthorId = commentDto.authorId
                            userService.getUserDtoFromUserId(commentAuthorId)
                                .map { Optional.of(it) }
                                .switchIfEmpty(Mono.just(Optional.empty()))
                                .map { commentDto to it }
                        }
                        .map {
                            val commentDto = it.first
                            val commentAuthorDto = it.second.getOrNull()

                            Comment(
                                dto = commentDto,
                                profile = commentAuthorDto?.toProfile(followingIdSet.contains(commentAuthorDto.id)),
                            )
                        }
                        .collectList(),
                )
            }
            .flatMap { it }
    }

    private fun getFollowingIdSetOrEmpty(userId: Long?) =
        metaFolloweeFollowerService.getFollowingIds(userId)
            .collectList()
            .map { it.toSet() }
            .switchIfEmpty(Mono.just(emptySet()))

    fun deleteComment(slug: String, id: Long): Mono<Unit> = Mono
        .zip(
            userSessionProvider.getCurrentUserSession()
                .map { it.userDto }
                .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))),
            articleRepository.findBySlug(slug)
                .map { it.toDto() }
                .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.ARTICLE_NOT_FOUND))),
            commentRepository.findById(id)
                .map { it.toDto() }
                .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.COMMENT_NOT_FOUND))),
        )
        .publishOn(Schedulers.boundedElastic())
        .handle { it, sink ->
            val userDto = it.t1
            val articleDto = it.t2
            val commentDto = it.t3

            if (commentDto.authorId != userDto.id) {
                return@handle sink.error(InvalidRequestException(ErrorCode.USER_NOT_MATCHED))
            }

            if (commentDto.articleId != articleDto.id) {
                return@handle sink.error(InvalidRequestException(ErrorCode.ARTICLE_NOT_MATCHED))
            }

            commentRepository.delete(commentDto.toEntity()).subscribe()
        }
}
