package co.eware.webapp

import co.eware.repository.Repository
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Created by Ahmed Ibrahim on 17,April,2021
 */

private const val SIGNIN = "/signin"

@Location(SIGNIN)
data class SignIn(val userID: String = "", val error: String = "")

fun Route.signin(db:Repository, hashFunction:(String) -> String){
    get<SignIn> {
        call.respond(FreeMarkerContent("signin.ftl",null))
    }
}