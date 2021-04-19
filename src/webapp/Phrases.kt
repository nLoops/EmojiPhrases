package co.eware.webapp

import co.eware.model.EPSession
import co.eware.model.EmojiPhrase
import co.eware.model.User
import co.eware.redirect
import co.eware.repository.Repository
import co.eware.securityCode
import co.eware.verifyCode
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import java.lang.IllegalArgumentException

const val PHRASES = "/phrases"

@KtorExperimentalLocationsAPI
@Location(PHRASES)
class Phrases

@KtorExperimentalLocationsAPI
fun Route.phrases(db: Repository, hashFunction: (String) -> String) {
    get<Phrases> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
        if (user == null) {
            call.redirect(SignIn())
        } else {
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hashFunction)
            val phrases = db.phrases()
            call.respond(
                FreeMarkerContent(
                    "phrases.ftl", mapOf(
                        "phrases" to phrases,
                        "user" to user,
                        "date" to date,
                        "code" to code
                    ), user.userId
                )
            )
        }
    }

    post<Phrases> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
        val params = call.receiveParameters()
        val date = params["date"]?.toLongOrNull() ?: return@post call.redirect(it)
        val code = params["code"] ?: return@post call.redirect(it)
        val action = params["action"] ?: throw IllegalArgumentException("Missing parameter: action")

        if (user == null || !call.verifyCode(date, user, code, hashFunction)) {
            call.redirect(SignIn())
        }

        when (action) {
            "delete" -> {
                val id = params["id"] ?: throw IllegalArgumentException("Missing parameter: id")
                db.remove(id)
            }
            "add" -> {
                val emoji = params["emoji"] ?: throw IllegalArgumentException("Missing parameter: emoji")
                val phrase = params["phrase"] ?: throw IllegalArgumentException("Missing parameter: phrase")
                db.add(user!!.userId, emoji, phrase)
            }
        }
        call.redirect(Phrases())
    }

//    authenticate("auth") {
//        get<Phrases> {
//            // Get the user from Call Object
//            val user = call.authentication.principal as User
//            val phrases = db.phrases()
//            call.respond(
//                FreeMarkerContent(
//                    "phrases.ftl", mapOf(
//                        "phrases" to phrases,
//                        "displayName" to user.displayName
//                    )
//                )
//            )
//        }
//
//        post<Phrases> {
//            val params = call.receiveParameters()
//            val action = params["action"] ?: throw IllegalArgumentException("Missing parameter: action")
//            when (action) {
//                "delete" -> {
//                    val id = params["id"] ?: throw IllegalArgumentException("Missing parameter: id")
//                    db.remove(id)
//                }
//                "add" -> {
//                    val emoji = params["emoji"] ?: throw IllegalArgumentException("Missing parameter: emoji")
//                    val phrase = params["phrase"] ?: throw IllegalArgumentException("Missing parameter: phrase")
//                    db.add(emoji, phrase)
//                }
//            }
//            call.redirect(Phrases())
//        }
//    }
}