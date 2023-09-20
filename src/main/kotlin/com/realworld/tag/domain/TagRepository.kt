package com.realworld.tag.domain

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TagRepository : R2dbcRepository<Tag, Long> {
    fun findAllByName(name: String): Flux<Tag>
}
