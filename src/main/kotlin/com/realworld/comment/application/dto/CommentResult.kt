package com.realworld.comment.application.dto

import com.realworld.user.application.dto.UserDto

data class CommentResult(
    val commentDto: CommentDto,
    val authorDto: UserDto,
    val isSelfFollowing: Boolean,
)
