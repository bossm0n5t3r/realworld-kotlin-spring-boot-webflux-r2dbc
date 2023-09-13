package com.realworld.user.application

import com.realworld.exception.InvalidRequestException
import com.realworld.profile.dto.Profile
import com.realworld.profile.dto.ProfileWrapper
import com.realworld.security.UserPasswordEncoder
import com.realworld.security.UserSessionProvider
import com.realworld.security.UserTokenProvider
import com.realworld.user.domain.User
import com.realworld.user.domain.UserRepository
import com.realworld.user.presentation.dto.AuthenticationUser
import com.realworld.user.presentation.dto.SignInRequest
import com.realworld.user.presentation.dto.SignUpRequest
import com.realworld.user.presentation.dto.UpdateRequest
import com.realworld.user.presentation.dto.UserWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userPasswordEncoder: UserPasswordEncoder,
    private val userTokenProvider: UserTokenProvider,
    private val userSessionProvider: UserSessionProvider,
) {
    private fun Mono<UserWrapper<SignUpRequest>>.isValid(): Mono<UserWrapper<SignUpRequest>> {
        return this
            .publishOn(Schedulers.boundedElastic())
            .handle { request, sink ->
                val signUpRequest = request.user
                var isError = false
                userRepository.existsByEmail(signUpRequest.email).subscribe {
                    if (it) {
                        isError = true
                        sink.error(InvalidRequestException("email", "already exists"))
                        return@subscribe
                    }
                }
                userRepository.existsByUsername(signUpRequest.username).subscribe {
                    if (it) {
                        isError = true
                        sink.error(InvalidRequestException("username", "already exists"))
                        return@subscribe
                    }
                }
                if (isError) return@handle
                sink.next(request)
            }
    }

    @Transactional
    fun signUp(request: Mono<UserWrapper<SignUpRequest>>): Mono<UserWrapper<AuthenticationUser>> {
        return request
            .isValid()
            .doOnError { throw it }
            .map {
                val signUpRequest = it.user
                User(
                    email = signUpRequest.email,
                    username = signUpRequest.username,
                    encodedPassword = userPasswordEncoder.encode(signUpRequest.password),
                )
            }
            .flatMap { userRepository.save(it) }
            .map { user ->
                val token = userTokenProvider.generateToken(user.id?.toString())
                user.toAuthenticationUser(token)
            }
            .map { authenticationUser -> authenticationUser.withUserWrapper() }
    }

    fun signIn(signInRequest: Mono<UserWrapper<SignInRequest>>): Mono<UserWrapper<AuthenticationUser>> {
        return signInRequest
            .publishOn(Schedulers.boundedElastic())
            .handle { request, sink ->
                val email = request.user.email
                val password = request.user.password
                var found = false
                userRepository.findAllByEmail(email)
                    .subscribe {
                        if (userPasswordEncoder.matches(password, it.encodedPassword)) {
                            found = true
                            val token = userTokenProvider.generateToken(it.id?.toString())
                            sink.next(it.toAuthenticationUser(token).withUserWrapper())
                            return@subscribe
                        }
                    }
                if (found) return@handle
                sink.error(InvalidRequestException("User", "not found"))
            }
            .doOnError { throw it }
    }

    fun getUser(): Mono<UserWrapper<AuthenticationUser>> {
        return userSessionProvider.getCurrentUserSession()
            .map { it.user.toAuthenticationUser(it.token).withUserWrapper() }
    }

    fun update(request: Mono<UserWrapper<UpdateRequest>>): Mono<UserWrapper<AuthenticationUser>> {
        return request
            .zipWith(userSessionProvider.getCurrentUserSession())
            .map {
                val updateRequest = it.t1.user
                val currentUser = it.t2.user

                User(
                    id = currentUser.id,
                    username = resolveUsername(currentUser, updateRequest.username),
                    email = resolveEmail(currentUser, updateRequest.email),
                    encodedPassword = resolveEncodedPassword(currentUser, updateRequest.password),
                    bio = updateRequest.bio ?: currentUser.bio,
                    image = updateRequest.image ?: currentUser.bio,
                    followingIds = currentUser.followingIds,
                    favoriteArticlesIds = currentUser.favoriteArticlesIds,
                )
            }
            .flatMap { userRepository.save(it) }
            .map { user ->
                val token = userTokenProvider.generateToken(user.id?.toString())
                user.toAuthenticationUser(token)
            }
            .map { authenticationUser -> authenticationUser.withUserWrapper() }
    }

    private fun resolveUsername(user: User, newUsername: String?): String {
        if (newUsername.isNullOrBlank() || user.username == newUsername) {
            return user.username
        }
        userRepository.existsByUsername(newUsername).subscribe {
            if (it) throw InvalidRequestException("username", "already exists")
        }
        return newUsername
    }

    private fun resolveEmail(user: User, newEmail: String?): String {
        if (newEmail.isNullOrBlank() || user.email == newEmail) {
            return user.email
        }
        userRepository.existsByEmail(newEmail).subscribe {
            if (it) throw InvalidRequestException("email", "already exists")
        }
        return newEmail
    }

    private fun resolveEncodedPassword(user: User, newPassword: String?): String {
        return if (newPassword.isNullOrBlank()) {
            user.encodedPassword
        } else {
            userPasswordEncoder.encode(newPassword)
        }
    }

    fun getProfile(username: String): Mono<ProfileWrapper<Profile>> {
        return userRepository.findAllByUsername(username)
            .next()
            .switchIfEmpty(Mono.error(InvalidRequestException("username", "not found")))
            .zipWith(
                userSessionProvider.getCurrentUserSession()
                    .map { it.user.followingIdList }
                    .switchIfEmpty(Mono.just(emptyList())),
            )
            .map {
                val user = it.t1
                val followingIdListOfViewer = it.t2

                Profile(
                    username = user.username,
                    bio = user.bio,
                    image = user.image,
                    following = followingIdListOfViewer.contains(user.id),
                )
            }
            .map { ProfileWrapper(it) }
    }
}
