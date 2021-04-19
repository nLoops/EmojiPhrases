package co.eware.webapp

import co.eware.MIN_PASSWORD_LENGTH
import co.eware.MIN_USER_ID_LENGTH
import co.eware.model.EPSession
import co.eware.redirect
import co.eware.repository.Repository
import co.eware.userNameValid
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

/**
 * Created by Ahmed Ibrahim on 17,April,2021
 */

private const val SIGNIN = "/signin"

@Location(SIGNIN)
data class SignIn(val userID: String = "", val error: String = "")

@KtorExperimentalLocationsAPI
fun Route.signin(db: Repository, hashFunction: (String) -> String) {
    get<SignIn> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }

        if (user != null) {
            call.redirect(Home())
        } else {
            call.respond(FreeMarkerContent("signin.ftl", mapOf("userId" to it.userID, "error" to it.error)))
        }
    }

    post<SignIn> {
        val signInParams = call.receive<Parameters>()
        val userId = signInParams["userId"] ?: return@post call.redirect(it)
        val password = signInParams["password"] ?: return@post call.redirect(it)

        val signInError = SignIn(userId)
        val signin = when {
            userId.length < MIN_USER_ID_LENGTH -> null
            password.length < MIN_PASSWORD_LENGTH -> null
            !userNameValid(userId) -> null
            else -> db.user(userId, hashFunction(password))
        }

        if (signin == null) {
            call.redirect(signInError.copy(error = "Invalid username or password"))
        } else {
            call.sessions.set(EPSession(signin.userId))
            call.redirect(Phrases())
        }


    }

}