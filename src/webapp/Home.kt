package co.eware.webapp

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val HOME = "/"

@KtorExperimentalLocationsAPI
@Location(HOME)
class Home

@KtorExperimentalLocationsAPI
fun Route.home() {
    get<Home> {
        call.respond(FreeMarkerContent("home.ftl", null))
    }
}