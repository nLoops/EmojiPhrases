package co.eware.webapp

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val ABOUT = "/about"

@KtorExperimentalLocationsAPI
@Location(ABOUT)
class About

@KtorExperimentalLocationsAPI
fun Route.about() {
    get<About> {
        call.respond(FreeMarkerContent("about.ftl", null))
    }
}