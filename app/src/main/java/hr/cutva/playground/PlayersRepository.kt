package hr.cutva.playground

import android.content.Context
import hr.cutva.playground.api.PlayersApi
import hr.cutva.playground.db.CacheDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PlayersRepository(
    private val cacheDatabase: CacheDatabase,
    private val playersApi: PlayersApi
) {

    fun all(): Flow<List<Player>> {
        return flow {
            val lastCacheDate = cacheDatabase.getPlayersLastCacheDate()
            emitAll(playersApi.all(lastCacheDate))
        }.map {
            cacheDatabase.savePlayers(it)
            cacheDatabase.getPlayers()
        }.map { players -> players.filterNot { it.deleted } }
    }

    suspend fun add(player: Player): Result<Player> {
        return playersApi.add(player)
    }

    suspend fun update(player: Player): Result<Player> {
        return playersApi.update(player)
    }

    suspend fun delete(player: Player): Result<Player> {
        return playersApi.update(player.copy(deleted = true))
    }


    companion object {
        fun create(context: Context): PlayersRepository {
            return PlayersRepository(
                cacheDatabase = CacheDatabase.create(context),
                playersApi = PlayersApi()
            )
        }
    }

}