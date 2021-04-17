package co.eware.webapp

import co.eware.redirect
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*

/**
 * Created by Ahmed Ibrahim on 17,April,2021
 */
private const val SIGNOUT = "/signout"

@Location(SIGNOUT)
class SignOut

fun Route.signout(){
    get<SignOut> {
        call.redirect(SignIn())
    }
}

