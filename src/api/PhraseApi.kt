package co.eware.api

import co.eware.API_VERSION
import co.eware.api.requests.PhrasesApiRequest
import co.eware.apiUer
import co.eware.repository.Repository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASE_API_ENDPOINT = "$API_VERSION/phrases"

@Location(PHRASE_API_ENDPOINT)
class PhraseApi

@KtorExperimentalLocationsAPI
fun Route.phrasesApi(db: Repository) {
    authenticate("jwt") {
        get<PhraseApi> {
            call.respond(db.phrases())
        }

        post<PhraseApi> {
            val user = call.apiUer!!
            try {
                val request = call.receive<PhrasesApiRequest>()
                val phrase = db.add(user.userId, request.emoji, request.phrase)
                if (phrase != null) {
                    call.respond(phrase)
                } else {
                    call.respondText("Invalid data received.", status = HttpStatusCode.InternalServerError)
                }
            } catch (e: Throwable) {
                call.respondText("Invalid data received.", status = HttpStatusCode.BadRequest)
            }
        }
    }
}