package community.flock.jedi.define

import arrow.core.Either
import community.flock.AppException
import community.flock.common.define.Repository
import community.flock.jedi.data.Jedi
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface JediRepository : Repository {
    suspend fun getJediByUUID(uuid: UUID): Either<AppException, Jedi>

    suspend fun getAllJedi(): Either<AppException, Flow<Jedi>>
}