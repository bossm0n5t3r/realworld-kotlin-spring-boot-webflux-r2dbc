package com.realworld.meta.domain

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface MetaArticleTagRepository : R2dbcRepository<MetaArticleTag, Long>
