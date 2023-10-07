package com.realworld.article.domain

import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ArticleRepository : R2dbcRepository<ArticleEntity, Long> {
    fun findAllByAuthorIdIn(authorIds: Collection<Long>, pageable: Pageable): Flux<ArticleEntity>
    fun findBySlug(slug: String): Mono<ArticleEntity>
}
