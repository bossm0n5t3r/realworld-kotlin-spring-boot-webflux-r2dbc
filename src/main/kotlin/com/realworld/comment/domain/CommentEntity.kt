package com.realworld.comment.domain

import com.realworld.comment.application.dto.CommentDto
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("comment_info")
data class CommentEntity(
    @Id
    val id: Long? = null,
    @CreatedDate
    var createdAt: Instant,
    @LastModifiedDate
    var updatedAt: Instant,
    var authorId: Long,
    var articleId: Long,
    var body: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommentEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    constructor(dto: CommentDto) : this(
        id = dto.id,
        createdAt = dto.createdAt,
        updatedAt = dto.updatedAt,
        authorId = dto.authorId,
        articleId = dto.articleId,
        body = dto.body,
    )

    fun toDto() = CommentDto(this)
}
