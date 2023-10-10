package com.realworld.comment.presentation

import com.realworld.comment.application.CommentService
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
}
