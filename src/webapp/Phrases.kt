package co.eware.webapp

import co.eware.model.EmojiPhrase
import co.eware.model.User
import co.eware.redirect
import co.eware.repository.Repository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.lang.IllegalArgumentException

const val PHRASES = "/phrases"

@KtorExperimentalLocationsAPI
@Location(PHRASES)
class Phrases

@KtorExperimentalLocationsAPI
fun Route.phrases(db: Repository) {
    authenticate("auth") {
        get<Phrases> {
            // Get the user from Call Object
            val user = call.authentication.principal as User
            val phrases = db.phrases()
            call.respond(
                FreeMarkerContent(
                    "phrases.ftl", mapOf(
                        "phrases" to phrases,
                        "displayName" to user.displayName
                    )
                )
            )
        }

        post<Phrases> {
            val params = call.receiveParameters()
            val action = params["action"] ?: throw IllegalArgumentException("Missing parameter: action")
            when (action) {
                "delete" -> {
                    val id = params["id"] ?: throw IllegalArgumentException("Missing parameter: id")
                    db.remove(id)
                }
                "add" -> {
                    val emoji = params["emoji"] ?: throw IllegalArgumentException("Missing parameter: emoji")
                    val phrase = params["phrase"] ?: throw IllegalArgumentException("Missing parameter: phrase")
                    db.add(emoji, phrase)
                }
            }
            call.redirect(Phrases())
        }
    }
}