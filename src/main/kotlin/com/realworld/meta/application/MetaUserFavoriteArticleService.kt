package com.realworld.meta.application

import com.realworld.meta.domain.MetaUserFavoriteArticle
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

    fun favoriteArticle(currentUserId: Long?, articleId: Long?): Mono<Boolean> {
        if (currentUserId == null || articleId == null) return Mono.just(false)
        return metaUserFavoriteArticleRepository.save(
            MetaUserFavoriteArticle(
                userId = currentUserId,
                favoriteArticleId = articleId,
            ),
        ).thenReturn(true)
    }

    fun unfavoriteArticle(currentUserId: Long?, articleId: Long?): Mono<Boolean> {
        if (currentUserId == null || articleId == null) return Mono.just(false)

        return metaUserFavoriteArticleRepository.findAllByUserIdAndFavoriteArticleId(
            userId = currentUserId,
            favoriteArticleId = articleId,
        )
            .collectList()
            .map {
                metaUserFavoriteArticleRepository.deleteAll(it)
            }
            .thenReturn(true)
    }
}
