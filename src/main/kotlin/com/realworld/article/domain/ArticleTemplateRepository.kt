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
        filteredIds: Set<Long>,
        authorId: Long?,
        limit: Int,
        offset: Long,
    ): Flux<Article> {
        val query = Query.query(
            CriteriaDefinition.from(
                getFilteredArticleIdsCriteria(filteredIds),
                getAuthorIdCriteria(authorId),
            ),
        )
            .offset(offset)
            .limit(limit)
            .sort(Sort.by(Sort.Direction.DESC, Article::createdAt.name))

        return r2dbcEntityTemplate.select(query, Article::class.java)
    }

    private fun getFilteredArticleIdsCriteria(filteredIds: Set<Long>) = if (filteredIds.isNotEmpty()) {
        Criteria.where(Article::id.name).`in`(filteredIds)
    } else {
        Criteria.empty()
    }

    private fun getAuthorIdCriteria(authorId: Long?) = if (authorId != null) {
        Criteria.where(Article::authorId.name).`is`(authorId)
    } else {
        Criteria.empty()
    }
}
