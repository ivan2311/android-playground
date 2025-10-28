package hr.cutva.playground.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(players: List<PlayerEntity>)

    @Query("SELECT * FROM players")
    suspend fun getAll(): List<PlayerEntity>


}