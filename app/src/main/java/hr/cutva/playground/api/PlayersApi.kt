package hr.cutva.playground.api

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import hr.cutva.playground.Player
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val COLLECTION_PLAYERS = "players"
private const val FIELD_MODIFIED_DATE = "modifiedDate"

class PlayersApi {

    private val api = Firebase.firestore

    fun all(fromDate: Date = Date(0)): Flow<List<Player>> {
        return callbackFlow {
            api.collection(COLLECTION_PLAYERS)
                .whereGreaterThan(FIELD_MODIFIED_DATE, Timestamp(fromDate))
                .addSnapshotListener { value, error ->
                    val players = value?.map { doc ->
                        doc.toObject(FBPlayer::class.java).toPlayer(doc.id)
                    } ?: emptyList()
                    trySend(players)
                }
            awaitClose {
                cancel()
            }
        }
    }

    suspend fun add(player: Player): Result<Player> = suspendCoroutine { cont ->
        api.collection(COLLECTION_PLAYERS)
            .add(player.toFBPlayer())
            .addOnSuccessListener { doc ->
                cont.resume(Result.success(player.copy(id = doc.id)))
            }
            .addOnFailureListener {
                cont.resume(Result.failure(it))
            }
    }

    suspend fun update(player: Player): Result<Player> = suspendCoroutine { cont ->
        api.collection(COLLECTION_PLAYERS)
            .document(player.id)
            .set(player.toFBPlayer())
            .addOnSuccessListener {
                cont.resume(Result.success(player))
            }
            .addOnFailureListener {
                cont.resume(Result.failure(it))
            }
    }

}