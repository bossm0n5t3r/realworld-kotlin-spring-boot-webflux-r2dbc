package com.realworld.comment.presentation.dto

import com.realworld.comment.application.dto.CommentDto
import com.realworld.exception.ErrorCode
import com.realworld.exception.InvalidRequestException
import com.realworld.user.presentation.dto.Profile
import java.time.Instant

data class Comment(
    val id: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val body: String,
    val author: Profile,
) {
    constructor(dto: CommentDto, profile: Profile? = null) : this(
        id = dto.id ?: throw InvalidRequestException(ErrorCode.COMMENT_NOT_FOUND),
        createdAt = dto.createdAt,
        updatedAt = dto.updatedAt,
        body = dto.body,
        author = profile ?: Profile(),
    )
}
