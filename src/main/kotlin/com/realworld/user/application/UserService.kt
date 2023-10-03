package com.realworld.user.application

import com.realworld.exception.ErrorCode
import com.realworld.exception.InvalidRequestException
import com.realworld.meta.application.MetaFolloweeFollowerService
import com.realworld.security.UserPasswordEncoder
import com.realworld.security.UserSession
import com.realworld.security.UserSessionProvider
import com.realworld.security.UserTokenProvider
import com.realworld.user.application.dto.SignInDto
import com.realworld.user.application.dto.SignUpDto
import com.realworld.user.application.dto.SignUpDto.Companion.toUserDto
import com.realworld.user.application.dto.UpdateUserDto
import com.realworld.user.application.dto.UserDto
import com.realworld.user.application.dto.UserDto.Companion.toDto
import com.realworld.user.domain.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userPasswordEncoder: UserPasswordEncoder,
    private val userTokenProvider: UserTokenProvider,
    private val userSessionProvider: UserSessionProvider,
    private val metaFolloweeFollowerService: MetaFolloweeFollowerService,
) {
    private fun Mono<SignUpDto>.isValid(): Mono<SignUpDto> {
        return this
            .publishOn(Schedulers.boundedElastic())
            .handle { signUpDto, sink ->
                var isError = false
                userRepository.existsByEmail(signUpDto.email).subscribe {
                    if (it) {
                        isError = true
                        sink.error(InvalidRequestException(ErrorCode.EMAIL_ALREADY_EXISTS))
                        return@subscribe
                    }
                }
                userRepository.existsByUsername(signUpDto.username).subscribe {
                    if (it) {
                        isError = true
                        sink.error(InvalidRequestException(ErrorCode.USERNAME_ALREADY_EXISTS))
                        return@subscribe
                    }
                }
                if (isError) return@handle
                sink.next(signUpDto)
            }
    }

    @Transactional
    fun signUp(signUpDto: Mono<SignUpDto>): Mono<Pair<UserDto, String>> {
        return signUpDto
            .isValid()
            .doOnError { throw it }
            .map { it.toUserDto(userPasswordEncoder.encode(it.password)).toEntity() }
            .flatMap { userRepository.save(it) }
            .map { it.toDto() to userTokenProvider.generateToken(it.id?.toString()) }
    }

    fun signIn(signInDto: Mono<SignInDto>): Mono<Pair<UserDto, String>> {
        return signInDto
            .publishOn(Schedulers.boundedElastic())
            .handle { request, sink ->
                val email = request.email
                val password = request.password
                var found = false
                userRepository.findAllByEmail(email)
                    .map { it.toDto() }
                    .subscribe {
                        if (userPasswordEncoder.matches(password, it.encodedPassword)) {
                            found = true
                            val token = userTokenProvider.generateToken(it.id?.toString())
                            sink.next(it to token)
                            return@subscribe
                        }
                    }
                if (found) return@handle
                sink.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))
            }
            .doOnError { throw it }
    }

    fun getUser(): Mono<UserSession> {
        return userSessionProvider.getCurrentUserSession()
    }

    fun update(request: Mono<UpdateUserDto>): Mono<Pair<UserDto, String>> {
        return request
            .zipWith(userSessionProvider.getCurrentUserSession())
            .map {
                val updateUserDto = it.t1
                val currentUserDto = it.t2.userDto
                currentUserDto.updateWith(updateUserDto)
            }
            .flatMap { userRepository.save(it.toEntity()) }
            .map { it.toDto() to userTokenProvider.generateToken(it.id?.toString()) }
    }

    private fun UserDto.updateWith(updateUserDto: UpdateUserDto) = UserDto(
        id = this.id,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        username = resolveUsername(this, updateUserDto.username),
        email = resolveEmail(this, updateUserDto.email),
        encodedPassword = resolveEncodedPassword(this, updateUserDto.password),
        bio = updateUserDto.bio ?: this.bio,
        image = updateUserDto.image ?: this.bio,
    )

    private fun resolveUsername(userDto: UserDto, newUsername: String?): String {
        if (newUsername.isNullOrBlank() || userDto.username == newUsername) {
            return userDto.username
        }
        userRepository.existsByUsername(newUsername).subscribe {
            if (it) throw InvalidRequestException(ErrorCode.USERNAME_ALREADY_EXISTS)
        }
        return newUsername
    }

    private fun resolveEmail(userDto: UserDto, newEmail: String?): String {
        if (newEmail.isNullOrBlank() || userDto.email == newEmail) {
            return userDto.email
        }
        userRepository.existsByEmail(newEmail).subscribe {
            if (it) throw InvalidRequestException(ErrorCode.EMAIL_ALREADY_EXISTS)
        }
        return newEmail
    }

    private fun resolveEncodedPassword(userDto: UserDto, newPassword: String?): String {
        return if (newPassword.isNullOrBlank()) {
            userDto.encodedPassword
        } else {
            userPasswordEncoder.encode(newPassword)
        }
    }

    fun getProfile(username: String): Mono<Pair<UserDto, Boolean>> {
        return userRepository.findAllByUsername(username)
            .next()
            .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USERNAME_NOT_FOUND)))
            .map { it.toDto() }
            .zipWith(
                userSessionProvider.getCurrentUserSession()
                    .map { metaFolloweeFollowerService.getFollowingIds(it.userDto.id) }
                    .switchIfEmpty(Mono.just(Flux.empty())),
            )
            .flatMap {
                val userDto = it.t1
                val followingIdListOfViewer = it.t2

                followingIdListOfViewer.any { followingId -> followingId == userDto.id }
                    .switchIfEmpty(Mono.just(false))
                    .map { following ->
                        userDto to following
                    }
            }
    }

    fun followUser(username: String): Mono<Pair<UserDto, Boolean>> {
        return userRepository.findAllByUsername(username)
            .next()
            .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND)))
            .map { it.toDto() }
            .zipWith(
                userSessionProvider.getCurrentUserSession()
                    .map { it.userDto }
                    .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))),
            )
            .flatMap {
                val followeeUserDto = it.t1
                val followerUserDto = it.t2

                metaFolloweeFollowerService.follow(
                    followeeUserId = followeeUserDto.id,
                    followerUserId = followerUserDto.id,
                ).map { following ->
                    followeeUserDto to following
                }
            }
    }

    fun unfollowUser(username: String): Mono<Pair<UserDto, Boolean>> {
        return userRepository.findAllByUsername(username)
            .next()
            .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND)))
            .map { it.toDto() }
            .zipWith(
                userSessionProvider.getCurrentUserSession()
                    .map { it.userDto }
                    .switchIfEmpty(Mono.error(InvalidRequestException(ErrorCode.USER_NOT_FOUND))),
            )
            .flatMap {
                val followeeUserDto = it.t1
                val followerUserDto = it.t2

                metaFolloweeFollowerService.unfollow(
                    followeeUserId = followeeUserDto.id,
                    followerUserId = followerUserDto.id,
                ).map { unfollow ->
                    followeeUserDto to unfollow.not()
                }
            }
    }

    fun getUserDtoFromUsername(username: String?): Mono<UserDto> {
        if (username == null) return Mono.empty()
        return userRepository.findAllByUsername(username)
            .next()
            .map { user -> user.toDto() }
    }

    fun getUserDtoFromUserId(userId: Long?): Mono<UserDto> {
        if (userId == null) return Mono.empty()
        return userRepository.findAllById(userId)
            .next()
            .map { user -> user.toDto() }
    }
}
