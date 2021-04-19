package co.eware

import co.eware.api.login
import co.eware.api.phrasesApi
import co.eware.model.EPSession
import co.eware.model.User
import co.eware.repository.DatabaseFactory
import co.eware.repository.EmojiPhrasesRepository
import co.eware.webapp.*
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import java.net.URI
import java.util.concurrent.TimeUnit

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)

    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(
                e.localizedMessage,
                ContentType.Text.Plain, HttpStatusCode.InternalServerError
            )
        }
    }

    install(ContentNegotiation) {
        gson()
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Sessions) {
        cookie<EPSession>("SESSION") {
            transform(SessionTransportTransformerMessageAuthentication(hashKey))
        }
    }

// Basic Auth.
//    install(Authentication) {
//        basic(name = "auth") {
//            realm = "Ktor server"
//            validate { credentials ->
//                if (credentials.password == "${credentials.name}123") User(credentials.name) else null
//            }
//        }
//    }

    install(Locations)

    val hashFunction = { s: String -> hash(s) }

    DatabaseFactory.init()

    val db = EmojiPhrasesRepository()
    val jwtServices = JwtServices()

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtServices.verifier)
            realm = "emojiphrases app"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asString()
                val user = db.userById(claimString)
                user
            }
        }
    }

    routing {
        // For Static content
        static("/static") {
            resources("images")
        }

        home(db)
        about(db)
        phrases(db, hashFunction)
        signin(db, hashFunction)
        signout()
        signup(db, hashFunction)

        // API
        login(db, jwtServices)
        phrasesApi(db)
    }
}

const val API_VERSION = "/api/v1"

@KtorExperimentalLocationsAPI
suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}

fun ApplicationCall.referHost() = request.header(HttpHeaders.Referrer)?.let { URI.create(it).host }

fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) =
    hashFunction("$date:${user.userId}:${request.host()}:${referHost()}")

fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) =
    securityCode(date, user, hashFunction) == code &&
            (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) }

val ApplicationCall.apiUer get() = authentication.principal<User>()