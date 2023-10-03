package com.realworld.meta.application

import com.realworld.meta.domain.MetaUserFavoriteArticleRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MetaUserFavoriteArticleService(
    private val metaUserFavoriteArticleRepository: MetaUserFavoriteArticleRepository,
) {
    fun getFavoriteArticleIds(userId: Long?): Mono<Set<Long>> {
        if (userId == null) return Mono.just(emptySet())
        return metaUserFavoriteArticleRepository.findAllByUserId(userId)
            .map { it.favoriteArticleId }
            .collectList()
            .map { it.toSet() }
    }
}
