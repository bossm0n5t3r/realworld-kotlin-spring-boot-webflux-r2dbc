package com.realworld.meta.application

import com.realworld.meta.domain.MetaFolloweeFollower
import com.realworld.meta.domain.MetaFolloweeFollowerRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class MetaFolloweeFollowerService(
    private val metaFolloweeFollowerRepository: MetaFolloweeFollowerRepository,
) {
    fun getFollowingIds(followerUserId: Long?): Flux<Long> {
        if (followerUserId == null) return Flux.empty()
        return metaFolloweeFollowerRepository.findAllByFollowerUserId(followerUserId)
            .map { it.followeeUserId }
    }

    fun isFollow(followeeUserId: Long?, followerUserId: Long?): Mono<Boolean> {
        if (followeeUserId == null || followerUserId == null) return Mono.just(false)
        return metaFolloweeFollowerRepository.existsByFolloweeUserIdAndFollowerUserId(
            followeeUserId = followeeUserId,
            followerUserId = followerUserId,
        )
    }

    fun follow(followeeUserId: Long?, followerUserId: Long?): Mono<Boolean> {
        if (followeeUserId == null || followerUserId == null) return Mono.just(false)

        return metaFolloweeFollowerRepository.existsByFolloweeUserIdAndFollowerUserId(
            followeeUserId = followeeUserId,
            followerUserId = followerUserId,
        ).flatMap { exists ->
            if (exists) {
                Mono.just(true)
            } else {
                val now = Instant.now()
                val metaFolloweeFollower = MetaFolloweeFollower(
                    createdAt = now,
                    updatedAt = now,
                    followeeUserId = followeeUserId,
                    followerUserId = followerUserId,
                )
                metaFolloweeFollowerRepository.save(metaFolloweeFollower)
                    .map { true }
                    .switchIfEmpty(Mono.just(false))
            }
        }
    }

    fun unfollow(followeeUserId: Long?, followerUserId: Long?): Mono<Boolean> {
        if (followeeUserId == null || followerUserId == null) return Mono.just(false)

        return metaFolloweeFollowerRepository.existsByFolloweeUserIdAndFollowerUserId(
            followeeUserId = followeeUserId,
            followerUserId = followerUserId,
        ).flatMap { exists ->
            if (exists) {
                val foundAll = metaFolloweeFollowerRepository.findAllByFolloweeUserIdAndFollowerUserId(
                    followeeUserId = followeeUserId,
                    followerUserId = followerUserId,
                )
                metaFolloweeFollowerRepository.deleteAll(foundAll)
                    .then(Mono.just(true))
            } else {
                Mono.just(true)
            }
        }
    }
}
