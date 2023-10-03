package com.realworld.meta.domain

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface MetaArticleTagRepository : R2dbcRepository<MetaArticleTag, Long> {
    fun findAllByArticleId(articleId: Long): Flux<MetaArticleTag>
    fun findAllByTagIdIn(tagIds: Collection<Long>): Flux<MetaArticleTag>
}
