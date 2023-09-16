package com.realworld.meta.domain

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface MetaFolloweeFollowerRepository : R2dbcRepository<MetaFolloweeFollower, Long> {
    fun findAllByFollowerUserId(followerUserId: Long): Flux<MetaFolloweeFollower>
    fun existsByFolloweeUserIdAndFollowerUserId(followeeUserId: Long, followerUserId: Long): Mono<Boolean>
    fun findAllByFolloweeUserIdAndFollowerUserId(followeeUserId: Long, followerUserId: Long): Flux<MetaFolloweeFollower>
}
