package co.eware

import co.eware.model.User
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Ahmed Ibrahim on 20,April,2021
 */
class JwtServices {
    private val issuer = "emojiphrases"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.userId)
        .withExpiresAt(expiresAt())
        .sign(algorithm)

    private fun expiresAt() =
        Date(System.currentTimeMillis() + 3_600_000 * 24)
    // 24 hours
    // SEARCH for refresh token cause the course didn't cover it.
    // here a link for refresh token https://stackoverflow.com/a/54378384


}