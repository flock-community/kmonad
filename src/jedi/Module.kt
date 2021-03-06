package community.flock.jedi

import arrow.core.getOrHandle
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import community.flock.AppException
import community.flock.common.LiveLayer.Companion.getLayer
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.Context
import community.flock.jedi.pipe.LiveRepository
import community.flock.jedi.pipe.bindDelete
import community.flock.jedi.pipe.bindGet
import community.flock.jedi.pipe.bindPost
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import kotlinx.coroutines.flow.toList

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    moduleWith(object : Context {
        override val jediRepository = LiveRepository(getLayer())
        override val logger = getLayer().logger
    })

}

fun Application.moduleWith(context: Context) {
    apiRouting {
        route("/jedi")
            .throws(InternalServerError, AppException.InternalServerError::class)
            .throws(BadRequest, AppException.BadRequest::class)
            .throws(NotFound, AppException.NotFound::class)
            .throws(Conflict, AppException.Conflict::class) {
                get<Unit, List<Jedi>> {
                    bindGet()
                        .run(context)
                        .getOrHandle { throw it }
                        .toList()
                        .let { respond(it) }
                }

                get<UuidParam, Jedi> { params ->
                    bindGet(params.uuid)
                        .run(context)
                        .getOrHandle { throw it }
                        .let { respond(it) }
                }

                post<Unit, Jedi, Jedi> { _, body ->
                    bindPost(body)
                        .run(context)
                        .getOrHandle { throw it }
                        .let { respond(it) }
                }

                delete<UuidParam, Jedi> { params ->
                    bindDelete(params.uuid)
                        .run(context)
                        .getOrHandle { throw it }
                        .let { respond(it) }
                }
            }
    }
}

@Path("/{uuid}")
data class UuidParam(@PathParam("UUID") val uuid: String)
