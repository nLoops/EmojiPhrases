package co.eware.api

import co.eware.JwtServices
import co.eware.hash
import co.eware.redirect
import co.eware.repository.Repository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Created by Ahmed Ibrahim on 20,April,2021
 */
const val LOGIN_ENDPOINT = "/login" // without API Version because the users need to aut
// before access to the API.

@Location(LOGIN_ENDPOINT)
class Login

@KtorExperimentalLocationsAPI
fun Route.login(db: Repository, jwtServices: JwtServices) {
    post<Login> {
        val params = call.receive<Parameters>()
        val userId = params["userId"] ?: return@post call.redirect(it)
        val password = params["password"] ?: return@post call.redirect(it)

        val user = db.user(userId, hash(password))
        if (user != null) {
            val token = jwtServices.generateToken(user)
            call.respondText(token)
        }else{
            call.respondText("Invalid user credential")
        }

    }
}