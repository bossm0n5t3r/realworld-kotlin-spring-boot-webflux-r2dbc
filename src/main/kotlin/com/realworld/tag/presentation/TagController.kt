package com.realworld.tag.presentation

import com.realworld.tag.application.TagService
import com.realworld.tag.presentation.dto.Tags
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class TagController(
    private val tagService: TagService,
) {
    @GetMapping("/api/tags")
    fun getTags(): Mono<Tags> = tagService.getTags()
        .map { it.map { tagDto -> tagDto.name } }
        .map { Tags(it) }
}
