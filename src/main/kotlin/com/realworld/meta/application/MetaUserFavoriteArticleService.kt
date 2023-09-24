package com.realworld.meta.application

import com.realworld.meta.domain.MetaUserFavoriteArticleRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class MetaUserFavoriteArticleService(
    private val metaUserFavoriteArticleRepository: MetaUserFavoriteArticleRepository,
) {
    fun getFavoriteArticleIds(userId: Long?): Optional<Set<Long>> {
        if (userId == null) return Optional.empty()
        return metaUserFavoriteArticleRepository.findAllByUserId(userId)
            .map { it.favoriteArticleId }
            .collectList()
            .map { it.toSet() }
            .blockOptional()
    }
}
