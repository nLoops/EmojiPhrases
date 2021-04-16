package co.eware.webapp

import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*

const val HOME = "/"

fun Route.home(){
    get(HOME){
        call.respond(FreeMarkerContent("home.ftl",null))
    }
}