package hr.cutva.playground.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import hr.cutva.playground.Player
import java.util.Date

@Database(entities = [PlayerEntity::class, CacheInfoEntity::class], version = 1)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun cacheInfoDao(): CacheInfoDao

    suspend fun getPlayers(): List<Player> {
        return playerDao().getAll().map { it.toPlayer() }
    }

    @Transaction
    suspend fun savePlayers(players: List<Player>) {
        if (players.isEmpty()) return
        playerDao().insert(players.map { it.toPlayerEntity() })
        savePlayersLastCacheDate(players.maxOf { it.modifiedDate })
    }

    private suspend fun savePlayersLastCacheDate(date: Long) {
        cacheInfoDao().save(
            CacheInfoEntity(
                table = TABLE_PLAYERS,
                lastCacheDate = date
            )
        )
    }

    suspend fun getPlayersLastCacheDate(): Date {
        return cacheInfoDao().get(TABLE_PLAYERS)?.let { Date(it.lastCacheDate) } ?: Date(0)
    }

    companion object Companion {
        fun create(appContext: Context): CacheDatabase {
            return Room.databaseBuilder(
                appContext,
                CacheDatabase::class.java,
                "db-cache"
            ).fallbackToDestructiveMigration(true)
                .build()
        }
    }
}