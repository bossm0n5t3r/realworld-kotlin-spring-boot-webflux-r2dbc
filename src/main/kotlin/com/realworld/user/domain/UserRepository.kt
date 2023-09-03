package com.realworld.user.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<User, Long> {
    fun existsByEmail(email: String): Mono<Boolean>
    fun existsByUsername(username: String): Mono<Boolean>
}
