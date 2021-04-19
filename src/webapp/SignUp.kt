package co.eware.webapp

import co.eware.MIN_PASSWORD_LENGTH
import co.eware.MIN_USER_ID_LENGTH
import co.eware.model.EPSession
import co.eware.model.User
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
private const val SIGNUP = "/signup"

@Location(SIGNUP)
data class SignUp(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val error: String = ""
)

@KtorExperimentalLocationsAPI
fun Route.signup(db: Repository, hashFunction: (String) -> String) {
    get<SignUp> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
        if (user != null) {
            call.redirect(Phrases())
        } else {
            call.respond(FreeMarkerContent("signup.ftl", mapOf("error" to it.error)))
        }
    }

    post<SignUp> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
        if (user != null) return@post call.redirect(Phrases())

        val signupParams = call.receive<Parameters>()
        val userId = signupParams["userId"] ?: return@post call.redirect(it)
        val password = signupParams["password"] ?: return@post call.redirect(it)
        val displayName = signupParams["displayName"] ?: return@post call.redirect(it)
        val email = signupParams["email"] ?: return@post call.redirect(it)

        val signUpError = SignUp(userId, displayName, email)
        when {
            password.length < MIN_PASSWORD_LENGTH ->
                call.redirect(signUpError.copy(error = "Password should be at least $MIN_PASSWORD_LENGTH characters long."))

            userId.length < MIN_USER_ID_LENGTH ->
                call.redirect(signUpError.copy(error = "User ID should be at least $MIN_USER_ID_LENGTH characters long."))

            !userNameValid(userId) ->
                call.redirect(signUpError.copy(error = "Username should consist of digits, letters, dots or underscores."))

            db.user(userId) != null ->
                call.redirect(signUpError.copy(error = "User with the following username is already registered."))

            else -> {
                val hash = hashFunction(password)
                val newUser = User(userId, email, displayName, hash)

                try {
                    db.createUser(newUser)
                } catch (e: Throwable) {
                    when {
                        db.user(userId) != null ->
                            call.redirect(signUpError.copy(error = "User with the following username is already registered."))

                        db.userByEmail(email) != null ->
                            call.redirect(signUpError.copy(error = "User with the following email $email is already registered."))

                        else -> {
                            application.log.error("Failed to register user", e)
                            call.redirect(signUpError.copy(error = "Failed to register."))
                        }
                    }
                }
                call.sessions.set(EPSession(newUser.userId))
                call.redirect(Phrases())
            }

        }
    }

}
