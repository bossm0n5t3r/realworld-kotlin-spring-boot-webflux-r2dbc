package com.realworld.comment.domain

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : R2dbcRepository<CommentEntity, Long>
