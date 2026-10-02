package com.example.avnil_rapidrecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.avnil_rapidrecall.ui.theme.AvnilrapidrecallTheme
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * MainActivity
 *
 * Purpose: One GameViewModel is made and pass it to App() which begins the switching between menu, game, log, and summary screens
 *
 * Design Rationale: Make one GameViewModel and then passed between all screens to share the same data.
 * The screens themselves are meant for display only and pass actual interactions to the GameViewModel, they are not allowed
 * to make any changes to the data in the game on their own.
 *
 * Outstanding Issues: All attempt data is lost when exiting the app
 * but this is allowed as per the assignment instructions.
 */
class MainActivity : ComponentActivity() {
    private val game: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvnilrapidrecallTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.LightGray
                ) {
                    App(game)
                }
            }
        }
    }
}

/**
 * Screen
 *
 * Purpose: Represent the four screens of the game, which are the start screen, the game screen,
 * the log screen, and the summary screen.
 *
 * Design Rationale: Used enum instead of strings in order to prevent possible typos from stopping
 * the screen from displaying anything, allows for four distinct screens to be displayed.
 *
 * Outstanding Issues: None
 */
enum class Screen { Start, Game, Log, Summary }

@Composable
fun App(game: GameViewModel) {
    // which screen is showing, kept after rotating screen
    // "Saveable" (suggested by Claude, Anthropic AI assistant, "keeping state through rotation", 2026-10-02)
    var screen by rememberSaveable { mutableStateOf(Screen.Start) }

    // leaving any screen resets the game so no timer is left running
    val goHome = {
        game.reset()
        screen = Screen.Start
    }

    // the back button will go to the start screen instead of closing the whole app
    // (suggested by Claude, Anthropic AI assistant, "navigation between screens", 2026-09-29)
    BackHandler(enabled = screen != Screen.Start) { goHome() }

    when (screen) {
        Screen.Start -> StartScreen(goTo = { screen = it })
        Screen.Game -> GameScreen(game, goBack = goHome)
        Screen.Log -> LogScreen(game, goBack = goHome)
        Screen.Summary -> SummaryScreen(game, goBack = goHome)
    }
}

// ============================================================== START SCREEN =======================================================================================
@Composable
fun StartScreen(goTo: (Screen) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "RapidRecall",
            fontSize = 42.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Memory Game App",
            modifier = Modifier.padding(bottom = 32.dp),
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(8.dp),
            onClick = { goTo(Screen.Game) }
        ) {
            Text("Start", fontSize = 22.sp)
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(8.dp),
            onClick = { goTo(Screen.Log) }
        ) {
            Text("Log", fontSize = 22.sp)
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(8.dp),
            onClick = { goTo(Screen.Summary) }
        ) {
            Text("Attempt Summary", fontSize = 22.sp)
        }
    }
}

// ============================================================== GAMEPLAY =======================================================================================
@Composable
fun GameScreen(game: GameViewModel, goBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // draw whichever stage the game is in
        when (game.state) {
            GameState.Pick -> PickDifficulty(game)
            GameState.Show -> Text(game.onscreenDigit, fontSize = 96.sp)
            GameState.Input -> InputStage(game)
            GameState.Result -> ResultStage(game)
        }
        // dont want back button bobbing up and down while the numbers are showing on the screen
        if (game.state != GameState.Show) {
            Button(onClick = goBack) { Text("Back") } // draw back for every stage in gane screen except for show
        }
    }
}

@Composable
fun PickDifficulty(game: GameViewModel) { // pick how hard/how many numbers you want in your guessing game
    var length by remember { mutableIntStateOf(5) } // just start in middlest of the range

    Text(
        "Pick the length of the sequence.",
        fontSize = 24.sp
    )
    Text(
        "1-10 digits long",
        Modifier.padding(bottom = 32.dp)
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { if (length > 1) length-- }) { Text("-", fontSize = 18.sp) }
        Text("$length", fontSize = 48.sp, modifier = Modifier.padding(horizontal = 18.dp))
        Button(onClick = { if (length < 10) length++ }) { Text("+", fontSize = 18.sp) }
    }
    Button(
        onClick = { game.gameStart(length) },
        modifier = Modifier.padding(top = 16.dp)
    ) { Text("Start") }
}

@Composable
fun InputStage(game: GameViewModel) {
    Text("Please type your guess now.", fontSize = 24.sp)

    OutlinedTextField(
        value = game.input,
        onValueChange = { game.filterGuess(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.padding(16.dp)
    )
    Button(onClick = { game.submit() }) { Text("Submit") }
}

// how a guess is shown on screen, an empty guess is shown as N/A
fun guessText(guess: String) = guess.ifEmpty { "N/A" }

@Composable
fun ResultStage(game: GameViewModel) {
    val lastAttempt = game.lastAttempt ?: return

    Text(
        if (lastAttempt.isAnswer) "Correct" else "Incorrect",
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = if (lastAttempt.isAnswer) Color(0xFF2E7D32) else Color.Red // same as i did in last assignment just to add a pop of colour
    )
    Text(
        "Answer: ${lastAttempt.answer}",
        modifier = Modifier.padding(bottom = 4.dp),
        fontSize = 24.sp
    )
    Text(
        "Your guess was: ${guessText(lastAttempt.guess)}",
        fontSize = 24.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Button(onClick = { game.reset() }) { Text("Play Again") }
}


// ========================================================== LOG SCREEN ==========================================================================================
@Composable
fun LogScreen(game: GameViewModel, goBack: () -> Unit) {
    val attempts = game.getAttempts()
    val timeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Log",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 32.dp)
        ) // padding top because some phones may have the holepunch camera covering it up
        // no attempts from the player
        if (attempts.isEmpty()) {
            Text("You have not made any attempts.", modifier = Modifier.padding(16.dp))
        }
        // scrollable list of attempts
        LazyColumn(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
        ) {
            items(attempts) { attempt ->
                AttemptRow(attempt, timeFormat)
            }
        }

        Button(onClick = goBack) { Text("Back") }
    }
}

// shareable format for each entry in the log
@Composable
fun AttemptRow(attempt: Attempts, timeFormat: SimpleDateFormat) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("${attempt.length} digit(s)")
        Text("Answer: ${attempt.answer}")
        Text("Guess: ${guessText(attempt.guess)}")
        Text(
            if (attempt.isAnswer) "Correct" else "Incorrect",
            fontWeight = FontWeight.Bold,
            color = if (attempt.isAnswer) Color(0xFF2E7D32) else Color.Red
        ) // dark green for better visibitly on light gray background
        Text("Time: ${timeFormat.format(attempt.timestamp)}")
    }
    HorizontalDivider() // make each indiviudaul entry more distinct
}

// ========================================================== SUMMARY SCREEN ==========================================================================================
@Composable
fun SummaryScreen(game: GameViewModel, goBack: () -> Unit) {
    val percentage = game.accuracy() // acuraccy score
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Attempt Summary",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Text("Total attempts: ${game.total()}")
        Text("Correct attempts: ${game.correct()}")
        Text("Accuracy: ${"%.2f".format(percentage)}%") // format to 2 decimal places
        Button(onClick = goBack, modifier = Modifier.padding(top = 16.dp)) { Text("Back") }
    }
}

// =================================================================== PREVIEW ========================================================================================
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AvnilrapidrecallTheme {
        App(GameViewModel())
    }
}