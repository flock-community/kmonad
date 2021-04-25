package community.flock.sith

import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import community.flock.AppException
import community.flock.common.DataBase
import community.flock.common.Env.getProp
import community.flock.common.LiveLogger
import community.flock.common.UuidParam
import community.flock.common.define.Logger
import community.flock.sith.data.Sith
import community.flock.sith.define.Context
import community.flock.sith.pipe.LiveRepository.Companion.liveRepository
import community.flock.sith.pipe.bindDelete
import community.flock.sith.pipe.bindGet
import community.flock.sith.pipe.bindPost
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import kotlinx.coroutines.flow.toList

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val host = getProp("ktor.db.host", "localhost")

    moduleWith(object : Context {
        override val sithRepository = DataBase.instance(host).liveRepository()
        override val logger: Logger = LiveLogger
    })

}

fun Application.moduleWith(context: Context) {
    apiRouting {
        route("/sith")
            .throws(InternalServerError, AppException.InternalServerError::class)
            .throws(BadRequest, AppException.BadRequest::class)
            .throws(NotFound, AppException.NotFound::class) {
                get<Unit, List<Sith>> {
                    respond(context.bindGet().toList())
                }

                get<UuidParam, Sith> { params ->
                    respond(context.bindGet(params.uuid))
                }

                post<Unit, Sith, Sith> { _, body ->
                    respond(context.bindPost(body))
                }

                delete<UuidParam, Sith> { params ->
                    respond(context.bindDelete(params.uuid))
                }
            }
    }
}
