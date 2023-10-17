package com.realworld.tag.application

import com.realworld.tag.application.dto.TagDto
import com.realworld.tag.application.dto.TagDto.Companion.toDto
import com.realworld.tag.domain.TagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class TagService(
    private val tagRepository: TagRepository,
) {
    fun createTags(tagNames: Flux<String>): Flux<Long> {
        return tagNames
            .flatMap { tagName ->
                tagRepository.findAllByName(tagName)
                    .next()
                    .switchIfEmpty { tagRepository.save(TagDto(name = tagName).toEntity()) }
            }.mapNotNull { it.id }
    }

    fun getTags(): Mono<List<TagDto>> {
        return tagRepository.findAll()
            .collectList()
            .map { it.map { entity -> entity.toDto() } }
    }
}
