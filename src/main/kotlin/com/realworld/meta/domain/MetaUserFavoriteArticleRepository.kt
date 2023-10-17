package com.realworld.meta.domain

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface MetaUserFavoriteArticleRepository : R2dbcRepository<MetaUserFavoriteArticle, Long> {
    fun findAllByUserId(userId: Long): Flux<MetaUserFavoriteArticle>
    fun findAllByUserIdAndFavoriteArticleId(userId: Long, favoriteArticleId: Long): Flux<MetaUserFavoriteArticle>
}
