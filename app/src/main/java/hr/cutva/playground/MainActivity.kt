package hr.cutva.playground

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import hr.cutva.playground.ui.theme.PlaygroundTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var playersRepository: PlayersRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playersRepository = PlayersRepository.create(this)

        val playersFlow = playersRepository.all()

        enableEdgeToEdge()
        setContent {
            val players = playersFlow.collectAsStateWithLifecycle(emptyList()).value

            var selectedPlayer by remember { mutableStateOf<Player?>(null) }

            PlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        PlayersList(Modifier.weight(1f), players) {
                            selectedPlayer = it
                        }

                        selectedPlayer?.let {
                            EditPlayerBox(
                                player = it,
                                onEdit = this@MainActivity::editPlayer,
                                onDelete = this@MainActivity::deletePlayer,
                                onCancel = { selectedPlayer = null }
                            )
                        } ?: run { CreatePlayerBox { addPlayer(it) } }
                    }
                }
            }
        }
    }

    private fun addPlayer(playerName: String) {
        lifecycleScope.launch {
            playersRepository.add(Player(name = playerName))
        }
    }

    private fun editPlayer(player: Player) {
        lifecycleScope.launch {
            playersRepository.update(player)
        }
    }

    private fun deletePlayer(player: Player) {
        lifecycleScope.launch {
            playersRepository.delete(player)
        }
    }
}

@Composable
private fun PlayersList(
    modifier: Modifier,
    players: List<Player>,
    onSelectPlayer: (Player) -> Unit
) {
    LazyColumn(modifier) {
        items(players) {
            PlayerItem(Modifier.fillMaxWidth(), it, onSelectPlayer)
            HorizontalDivider()
        }
    }
}

@Composable
private fun CreatePlayerBox(modifier: Modifier = Modifier, onCreate: (playerName: String) -> Unit) {
    var playerName by remember { mutableStateOf("") }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier.weight(1f),
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Player name") }
        )
        Spacer(Modifier.width(12.dp))
        Button(
            onClick = { onCreate(playerName) }
        ) {
            Text("Create")
        }
    }

}

@Composable
private fun EditPlayerBox(
    modifier: Modifier = Modifier,
    player: Player,
    onEdit: (Player) -> Unit,
    onDelete: (Player) -> Unit,
    onCancel: () -> Unit
) {
    var playerName by remember { mutableStateOf(player.name) }

    Column(modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text("Player name") }
        )

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(
                onClick = { onCancel() }
            ) {
                Text("Cancel")
            }
            Button(onClick = { onDelete(player) }) {
                Text("Delete")
            }
            Button(
                onClick = { onEdit(player.copy(name = playerName)) }
            ) {
                Text("Update")
            }
        }
    }
}

@Composable
private fun PlayerItem(modifier: Modifier, player: Player, onClick: (Player) -> Unit) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Text(player.name, fontWeight = FontWeight.Bold)
            Text(SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(player.modifiedDate))
        }
        IconButton(modifier = Modifier.padding(horizontal = 12.dp), onClick = { onClick(player) }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit"
            )
        }
    }
}