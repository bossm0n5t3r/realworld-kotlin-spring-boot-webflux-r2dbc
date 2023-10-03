package com.realworld.meta.application

import com.realworld.meta.domain.MetaArticleTag
import com.realworld.meta.domain.MetaArticleTagRepository
import com.realworld.tag.domain.TagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MetaArticleTagService(
    private val tagRepository: TagRepository,
    private val metaArticleTagRepository: MetaArticleTagRepository,
) {
    fun getArticleIdsFromTagName(tagName: String?): Mono<Set<Long>> {
        if (tagName == null) return Mono.just(emptySet())
        return tagRepository.findAllByName(tagName)
            .collectList()
            .map { tagList -> tagList.mapNotNull { it.id }.toSet() }
            .flatMap { tagIdSet ->
                metaArticleTagRepository.findAllByTagIdIn(tagIdSet).map { it.articleId }.collectList()
            }
            .map { it.toSet() }
    }

    fun getTagsFromArticleId(articleId: Long?): Mono<List<String>> {
        if (articleId == null) return Mono.just(emptyList())
        return metaArticleTagRepository.findAllByArticleId(articleId)
            .map { it.tagId }
            .collectList()
            .flatMap { tagIds ->
                tagRepository.findAllByIdIsIn(tagIds).map { it.name }.collectList()
            }
    }

    fun saveArticleIdToTagIds(articleId: Long, tagIds: Flux<Long>) {
        tagIds.subscribe {
            metaArticleTagRepository.save(MetaArticleTag(articleId = articleId, tagId = it))
                .subscribe()
        }
    }
}
