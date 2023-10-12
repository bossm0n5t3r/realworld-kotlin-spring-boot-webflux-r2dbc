package com.realworld.comment.presentation

import com.realworld.comment.application.CommentService
import com.realworld.comment.presentation.dto.Comment
import com.realworld.comment.presentation.dto.CommentWrapper
import com.realworld.comment.presentation.dto.CommentsWrapper
import com.realworld.comment.presentation.dto.CreateCommentRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class CommentController(
    private val commentService: CommentService,
) {
    @PostMapping("/api/articles/{slug}/comments")
    fun createComment(
        @PathVariable slug: String,
        @RequestBody request: Mono<CommentWrapper<CreateCommentRequest>>,
    ): Mono<CommentWrapper<Comment>> = commentService.createComment(slug, request.map { it.comment.body })
        .map {
            CommentWrapper(
                Comment(
                    dto = it.commentDto,
                    profile = it.authorDto.toProfile(it.isSelfFollowing),
                ),
            )
        }

    @GetMapping("/api/articles/{slug}/comments")
    fun getComments(
        @PathVariable slug: String,
    ): Mono<CommentsWrapper> = commentService.getComments(slug).map { CommentsWrapper(it) }

    @DeleteMapping("/api/articles/{slug}/comments/{id}")
    fun deleteComment(
        @PathVariable slug: String,
        @PathVariable id: Long,
    ) = commentService.deleteComment(slug, id)
}
