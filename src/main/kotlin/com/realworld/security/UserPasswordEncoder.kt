package com.realworld.security

import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserPasswordEncoder : PasswordEncoder {
    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    override fun encode(rawPassword: CharSequence): String {
        return passwordEncoder.encode(rawPassword)
    }

    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
}
