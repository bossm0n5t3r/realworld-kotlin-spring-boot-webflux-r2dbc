package com.realworld.user.application

import com.realworld.user.domain.User
import com.realworld.user.dto.AuthenticationUser
import com.realworld.user.dto.UserWrapper

fun User.toAuthenticationUser(token: String) = AuthenticationUser(
    email = this.email,
    token = token,
    username = this.username,
    bio = this.bio,
    image = this.image,
)

fun AuthenticationUser.withUserWrapper() = UserWrapper(this)
