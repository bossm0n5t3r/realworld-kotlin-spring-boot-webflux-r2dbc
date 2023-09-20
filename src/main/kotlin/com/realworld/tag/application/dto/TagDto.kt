package com.realworld.tag.application.dto

import com.realworld.tag.domain.Tag
import java.time.Instant

data class TagDto(
    val id: Long? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val name: String,
) {
    fun toEntity() = Tag(
        id = this.id,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        name = this.name,
    )

    companion object {
        fun Tag.toDto() = TagDto(
            id = this.id,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            name = this.name,
        )
    }
}
