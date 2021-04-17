package co.eware.api

import co.eware.API_VERSION
import co.eware.model.EmojiPhrase
import co.eware.model.Request
import co.eware.repository.Repository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASE_ENDPOINT = "$API_VERSION/phrase"

fun Route.phrase(db: Repository) {

    post(PHRASE_ENDPOINT) {
        val request = call.receive<Request>()
        val phrase = db.add("", request.emoji, request.phrase)
        call.respond(phrase)
    }

//    authenticate("auth") {
//        post(PHRASE_ENDPOINT) {
//            val request = call.receive<Request>()
//            val phrase = db.add(request.emoji, request.phrase)
//            call.respond(phrase)
//        }
//    }
}