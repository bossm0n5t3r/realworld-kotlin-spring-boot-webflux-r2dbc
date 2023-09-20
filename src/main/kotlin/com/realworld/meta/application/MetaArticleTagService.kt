package com.realworld.meta.application

import com.realworld.meta.domain.MetaArticleTag
import com.realworld.meta.domain.MetaArticleTagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class MetaArticleTagService(
    private val metaArticleTagRepository: MetaArticleTagRepository,
) {
    fun saveArticleIdToTagIds(articleId: Long, tagIds: Flux<Long>) {
        tagIds.subscribe {
            metaArticleTagRepository.save(MetaArticleTag(articleId = articleId, tagId = it))
                .subscribe()
        }
    }
}
