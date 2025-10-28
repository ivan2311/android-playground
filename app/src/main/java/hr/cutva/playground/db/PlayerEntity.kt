package hr.cutva.playground.db

import androidx.compose.runtime.Composable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hr.cutva.playground.Player

const val TABLE_PLAYERS = "players"

@Entity(tableName = TABLE_PLAYERS)
data class PlayerEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "modifiedDate") val modifiedDate: Long,
    @ColumnInfo(name = "deleted") val deleted: Boolean = false
)

fun PlayerEntity.toPlayer() = Player(
    id = id,
    name = name,
    modifiedDate = modifiedDate,
    deleted = deleted
)

fun Player.toPlayerEntity() = PlayerEntity(
    id = id,
    name = name,
    modifiedDate = modifiedDate,
    deleted = deleted
)