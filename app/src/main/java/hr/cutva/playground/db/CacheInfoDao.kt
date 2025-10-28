package hr.cutva.playground.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface CacheInfoDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(cacheInfoEntity: CacheInfoEntity)

    @Query("SELECT * FROM cacheInfos WHERE `table` = :table")
    suspend fun get(table: String): CacheInfoEntity?

}