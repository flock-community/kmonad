package community.flock.wielders

import community.flock.AppException
import community.flock.common.DataBase
import community.flock.common.Env.getProp
import community.flock.common.LiveLogger
import community.flock.common.define.DB.StarWars
import community.flock.common.define.Logger
import community.flock.jedi.data.Jedi
import community.flock.sith.data.Sith
import community.flock.wielders.define.Context
import community.flock.wielders.pipe.bindGet
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import community.flock.jedi.pipe.LiveRepository as LiveJediRepository
import community.flock.sith.pipe.LiveRepository as LiveSithRepository

typealias Ctx = PipelineContext<Unit, ApplicationCall>

@ExperimentalCoroutinesApi
@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val host = getProp("ktor.db.host", "localhost")
    val db = DataBase.instance(host).client.getDatabase(StarWars.name)
    val jediCollection = db.getCollection<Jedi>()
    val sithCollection = db.getCollection<Sith>()

    moduleWith(object : Context {
        override val jediRepository = LiveJediRepository.instance(jediCollection)
        override val sithRepository = LiveSithRepository.instance(sithCollection)
        override val logger: Logger = LiveLogger
    })

}

@ExperimentalCoroutinesApi
fun Application.moduleWith(context: Context) {
    routing {
        get("/force-wielders") {
            handle { call.respond(context.bindGet().toList()) }
        }

        get("/force-wielders/{uuid}") {
            handle { call.respond(context.bindGet(call.parameters["uuid"])) }
        }
    }
}

suspend fun <R> Ctx.handle(block: suspend () -> R) = try {
    block()
} catch (e: AppException) {
    call.respond(
        when (e) {
            is AppException.BadRequest -> BadRequest
            is AppException.NotFound -> NotFound
            is AppException.InternalServerError -> InternalServerError
        },
        e.message ?: ""
    )
}