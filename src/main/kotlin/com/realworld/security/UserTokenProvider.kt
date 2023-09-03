package com.realworld.security

import com.realworld.exception.ValidationException
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers
import org.jose4j.jwe.JsonWebEncryption
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers
import org.jose4j.jwk.OctetKeyPairJsonWebKey
import org.jose4j.jwk.OkpJwkGenerator
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumer
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.springframework.stereotype.Component

@Component
class UserTokenProvider {
    companion object {
        private val senderJwk = OkpJwkGenerator.generateJwk(OctetKeyPairJsonWebKey.SUBTYPE_ED25519)
        private val receiverJwk = OkpJwkGenerator.generateJwk(OctetKeyPairJsonWebKey.SUBTYPE_X25519)
        private val jwsAlgConstraints = AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.PERMIT,
            AlgorithmIdentifiers.EDDSA,
        )

        private val jweAlgConstraints = AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.PERMIT,
            KeyManagementAlgorithmIdentifiers.ECDH_ES,
        )

        private val jweEncConstraints = AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.PERMIT,
            ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256,
        )

        private const val JWT = "JWT"
        private const val EXPIRATION_TIME_MINUTES = 10f
        private const val NOT_BEFORE_MINUTES = 2f

        val JWT_CONSUMER: JwtConsumer = JwtConsumerBuilder()
            .setRequireExpirationTime()
            .setMaxFutureValidityInMinutes(300)
            .setRequireSubject()
            .setDecryptionKey(receiverJwk.privateKey)
            .setVerificationKey(senderJwk.publicKey)
            .setJwsAlgorithmConstraints(jwsAlgConstraints)
            .setJweAlgorithmConstraints(jweAlgConstraints)
            .setJweContentEncryptionAlgorithmConstraints(jweEncConstraints)
            .build()
    }

    fun generateToken(userId: String?): String {
        requireNotNull(userId) { throw ValidationException("userId", "should not be null") }
        val claims = JwtClaims().apply {
            this.subject = userId.toString()
            this.setExpirationTimeMinutesInTheFuture(EXPIRATION_TIME_MINUTES)
            this.setGeneratedJwtId()
            this.setIssuedAtToNow()
            this.setNotBeforeMinutesInThePast(NOT_BEFORE_MINUTES)
        }

        val jws = JsonWebSignature().apply {
            this.payload = claims.toJson()
            this.key = senderJwk.privateKey
            this.keyIdHeaderValue = senderJwk.keyId
            this.algorithmHeaderValue = AlgorithmIdentifiers.EDDSA
        }
        val innerJwt = jws.compactSerialization

        val jwe = JsonWebEncryption().apply {
            this.algorithmHeaderValue = KeyManagementAlgorithmIdentifiers.ECDH_ES
            val encAlg = ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256
            this.encryptionMethodHeaderParameter = encAlg
            this.key = receiverJwk.publicKey
            this.keyIdHeaderValue = receiverJwk.keyId
            this.contentTypeHeaderValue = JWT
            this.payload = innerJwt
        }
        return jwe.compactSerialization
    }

    fun getUserId(token: String): Long {
        return JWT_CONSUMER.processToClaims(token).subject.toLong()
    }

    fun validate(token: String): JwtClaims = JWT_CONSUMER.processToClaims(token)
}
