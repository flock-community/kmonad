package community.flock.jedi.pipe

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import community.flock.AppException
import community.flock.AppException.Conflict
import community.flock.AppException.InternalServerError
import community.flock.AppException.NotFound
import community.flock.common.define.DB
import community.flock.common.define.HasDatabaseClient
import community.flock.common.define.HasLogger
import community.flock.jedi.data.Jedi
import community.flock.jedi.define.Repository
import kotlinx.coroutines.flow.Flow
import org.litote.kmongo.eq
import java.util.UUID

interface LiveRepositoryContext : HasDatabaseClient, HasLogger

class LiveRepository(ctx: LiveRepositoryContext) : Repository {

    private val collection = ctx.databaseClient.getDatabase(DB.StarWars.name).getCollection<Jedi>()

    override suspend fun getAll(): Either<InternalServerError, Flow<Jedi>> = either {
        !guard { collection.find().toFlow() }
    }

    override suspend fun getByUUID(uuid: UUID): Either<AppException, Jedi> = either {
        val maybeJedi = !guard { collection.findOne(Jedi::id eq uuid.toString()) }
        maybeJedi ?: !NotFound(uuid).left()
    }

    override suspend fun save(jedi: Jedi): Either<AppException, Jedi> = either {
        val uuid = UUID.fromString(jedi.id)
        val existingJedi = getByUUID(uuid)
        if (existingJedi.isRight()) !Conflict(uuid).left() else {
            val result = !guard { collection.insertOne(jedi) }
            val maybeJedi = jedi.takeIf { result.wasAcknowledged() }
            maybeJedi ?: !InternalServerError().left()
        }
    }

    override suspend fun deleteByUUID(uuid: UUID): Either<AppException, Jedi> = either {
        val jedi = !getByUUID(uuid)
        val result = !guard { collection.deleteOne(Jedi::id eq uuid.toString()) }
        val maybeJedi = jedi.takeIf { result.wasAcknowledged() }
        maybeJedi ?: !InternalServerError().left()
    }

}

private inline fun <R> guard(block: () -> R) = guardWith(::InternalServerError, block)

private inline fun <E : AppException, R> guardWith(errorBlock: (ex: Exception) -> E, block: () -> R) = try {
    block().right()
} catch (ex: Exception) {
    errorBlock(ex).left()
}
