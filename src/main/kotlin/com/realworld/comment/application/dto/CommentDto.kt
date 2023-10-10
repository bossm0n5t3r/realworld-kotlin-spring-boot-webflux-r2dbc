package com.realworld.comment.application.dto

import com.realworld.comment.domain.CommentEntity
import java.time.Instant

data class CommentDto(
    val id: Long? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val authorId: Long,
    val articleId: Long,
    val body: String,
) {
    constructor(entity: CommentEntity) : this(
        id = entity.id,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        authorId = entity.authorId,
        articleId = entity.articleId,
        body = entity.body,
    )

    fun toEntity() = CommentEntity(this)
}
