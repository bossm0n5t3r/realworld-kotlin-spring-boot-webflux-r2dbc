package com.realworld.meta.application

import com.realworld.meta.domain.MetaArticleTag
import com.realworld.meta.domain.MetaArticleTagRepository
import com.realworld.tag.domain.TagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.Optional

@Service
class MetaArticleTagService(
    private val tagRepository: TagRepository,
    private val metaArticleTagRepository: MetaArticleTagRepository,
) {
    fun getArticleIdsFromTagName(tagName: String?): Optional<Set<Long>> {
        if (tagName == null) return Optional.empty()
        return tagRepository.findAllByName(tagName)
            .collectList()
            .map { tagList -> tagList.mapNotNull { it.id }.toSet() }
            .flatMap { tagIdSet ->
                metaArticleTagRepository.findAllByTagIdIn(tagIdSet).map { it.articleId }.collectList()
            }
            .map { it.toSet() }
            .blockOptional()
    }

    fun saveArticleIdToTagIds(articleId: Long, tagIds: Flux<Long>) {
        tagIds.subscribe {
            metaArticleTagRepository.save(MetaArticleTag(articleId = articleId, tagId = it))
                .subscribe()
        }
    }
}
