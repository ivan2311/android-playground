package hr.cutva.playground.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cacheInfos")
data class CacheInfoEntity(
    @PrimaryKey @ColumnInfo("table") val table: String = "",
    @ColumnInfo("lastCacheDate") val lastCacheDate: Long = 0
)