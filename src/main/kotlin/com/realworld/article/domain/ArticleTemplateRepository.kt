package com.realworld.article.domain

import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.CriteriaDefinition
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class ArticleTemplateRepository(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    fun findNewestArticlesFilteredBy(
        filteredIds: Set<Long>? = null,
        authorId: Long? = null,
        limit: Int,
        offset: Long,
    ): Flux<ArticleEntity> {
        val query = Query.query(
            CriteriaDefinition.from(
                getFilteredArticleIdsCriteria(filteredIds),
                getAuthorIdCriteria(authorId),
            ),
        )
            .offset(offset)
            .limit(limit)
            .sort(Sort.by(Sort.Direction.DESC, ArticleEntity::createdAt.name))

        return r2dbcEntityTemplate.select(query, ArticleEntity::class.java)
    }

    private fun getFilteredArticleIdsCriteria(filteredIds: Set<Long>?) = if (filteredIds.isNullOrEmpty()) {
        Criteria.empty()
    } else {
        Criteria.where(ArticleEntity::id.name).`in`(filteredIds)
    }

    private fun getAuthorIdCriteria(authorId: Long?) = if (authorId != null) {
        Criteria.where(ArticleEntity::authorId.name).`is`(authorId)
    } else {
        Criteria.empty()
    }
}
