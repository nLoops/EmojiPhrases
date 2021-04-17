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
private const val SIGNUP = "/signup"

@Location(SIGNUP)
data class SignUp(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val error: String = ""
)

fun Route.signup(db: Repository, hashFunction: (String) -> String) {
    get<SignUp> {
        call.respond(FreeMarkerContent("signup.ftl", null))
    }

}
