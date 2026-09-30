package com.example.avnil_rapidrecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.avnil_rapidrecall.ui.theme.AvnilrapidrecallTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.activity.viewModels
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import kotlin.getValue
import androidx.compose.foundation.layout.Row


class MainActivity : ComponentActivity() {

    private val game: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvnilrapidrecallTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(game)
                }
            }
        }
    }
}

@Composable
fun App(game: GameViewModel) {
    // which screen is showing: "start", "game", "log", or "summary"
    var screen by rememberSaveable { mutableStateOf("start") }

    // leaving any screen resets the game so no timer is left running
    val goHome = {
        game.reset()
        screen = "start"
    }

    // phone back button goes to the start screen instead of closing the app
    BackHandler(enabled = screen != "start") { goHome() }

    when (screen) {
        "start" -> StartScreen(goTo = { screen = it })
        "game" -> GameScreen(game, goBack = goHome)
        "log" -> LogScreen(game, goBack = goHome)
        "summary" -> SummaryScreen(game, goBack = goHome)
    }
}

@Composable
fun StartScreen(goTo: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("RapidRecall", fontSize = 40.sp, fontWeight = FontWeight.Bold)

        Button(
            modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp),
            onClick = { goTo("game") }
        ) {
            Text("Start", fontSize = 22.sp)
        }
        Button(
            modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp),
            onClick = { goTo("log") }
        ) {
            Text("Log", fontSize = 22.sp)
        }
        Button(
            modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp),
            onClick = { goTo("summary") }
        ) {
            Text("Summary", fontSize = 22.sp)
        }
    }
}

@Composable
fun GameScreen(game: GameViewModel, goBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // draw whichever stage the game is in
        when (game.state) {
            GameState.Pick -> PickDifficulty(game)
            GameState.Show -> Text(game.onscreenDigit, fontSize = 96.sp)
            GameState.Input -> Text("INPUT HERE.")
            GameState.Result -> Text("RESULT HERE.")
        }
        Button(onClick = goBack) { Text("BACK") }
    }
}


@Composable
fun PickDifficulty(game: GameViewModel) { // pick how hard/how many numbers you want in your guessing game
    var length by rememberSaveable { mutableIntStateOf(4) }

    Text("How many digits?", fontSize = 24.sp)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { if (length > 1) length-- }) { Text("-") }
        Text("$length", fontSize = 48.sp, modifier = Modifier.padding(horizontal = 24.dp))
        Button(onClick = { if (length < 10) length++ }) { Text("+") }
    }

    Button(onClick = { game.gameStart(length) }) { Text("Start") }
}

// placeholder, list of attempts goes here later
@Composable
fun LogScreen(game: GameViewModel, goBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Log", fontSize = 32.sp)
        Button(onClick = goBack) { Text("Back") }
    }
}

// placeholder, stats go here later
@Composable
fun SummaryScreen(game: GameViewModel, goBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Attempt Summary", fontSize = 32.sp)
        Button(onClick = goBack) { Text("Back") }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AvnilrapidrecallTheme {
        App(GameViewModel())
    }
}