package hr.cutva.playground.api

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import hr.cutva.playground.Player

data class FBPlayer(
    @PropertyName("name") val name: String = "",
    @PropertyName("modifiedDate") @get:ServerTimestamp var modifiedDate: Timestamp? = null,
    @PropertyName("deleted") val deleted: Boolean = false
)

fun FBPlayer.toPlayer(id: String) = Player(
    id = id,
    name = name,
    modifiedDate = modifiedDate?.toDate()?.time ?: 0,
    deleted = deleted
)

fun Player.toFBPlayer() = FBPlayer(
    name = name,
    modifiedDate = null,
    deleted = deleted
)