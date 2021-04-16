package co.eware.webapp

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*

const val ABOUT = "/about"

fun Route.about(){
    get(ABOUT){
        call.respond(FreeMarkerContent("about.ftl",null))
    }
}