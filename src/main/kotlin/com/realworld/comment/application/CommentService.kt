package com.realworld.comment.application

import com.realworld.article.domain.ArticleRepository
import com.realworld.comment.application.dto.CommentDto
import com.realworld.comment.application.dto.CommentResult
import com.realworld.comment.domain.CommentRepository
import com.realworld.exception.ErrorCode
import com.realworld.exception.InvalidRequestException
import com.realworld.meta.application.MetaFolloweeFollowerService
import com.realworld.security.UserSessionProvider
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommentService(
    private val userSessionProvider: UserSessionProvider,
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val metaFolloweeFollowerService: MetaFolloweeFollowerService,
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
}
