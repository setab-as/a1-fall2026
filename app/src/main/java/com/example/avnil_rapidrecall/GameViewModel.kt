package com.example.avnil_rapidrecall

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * GameState
 *
 * Purpose: Represent the four states of the game round which are picking the difficulty, showing
 * the digits, inputting your guess, and then showing the answer and feedback on your attempt.
 *
 * Design Rationale: Fixed list ensures that the game is always in one of these states, the actual
 * game screen handles all four visually.
 *
 * Outstanding Issues: None
 */
enum class GameState { Pick, Show, Input, Result } // states of the game on actual game part

/**
 * GameViewModel
 *
 * Purpose: Run the core functionality of the game. Makes the sequence, then shows the digits one at
 * a time, ensures only digits are allowed in the input and caps input and length of the answer,
 * then saves each attempt for the log. It also makes sure to pass the accuracy
 * and total attempts to the summary screen, and checks the answer against the guess.
 *
 * Design Rationale: Keep all the game logic here instead of in the screens themselves,
 * to make sure that the screens sole responsibility is displaying the data that is processed here.
 * This class is the only one able to modify any of the games data and makes sure to hide the answer
 * from the screens until you submit your guess.
 *
 * Outstanding Issues: The game will treat any empty submission as an incorrect answer.
 */
class GameViewModel : ViewModel() {
    private val sequence = SequenceMaker()
    private val log = AttemptsLog()
    private var timer: Job? = null

    var state by mutableStateOf(GameState.Pick)
        private set // ui should only be able to read this same for rest below only viewmodel can change it
    private var answer = ""
    var onscreenDigit by mutableStateOf("")
        private set
    var input by mutableStateOf("")
        private set
    var lastAttempt by mutableStateOf<Attempts?>(null)
        private set

    fun gameStart(length: Int) { // make the sequence and show the process of it being flashed on screen before sending to input stage
        if (state != GameState.Pick) return

        answer = sequence.generate(length)
        state = GameState.Show
        input = ""
        // below is the process of showing each number in the answer one by one
        timer = viewModelScope.launch {
            for (digit in answer) {
                onscreenDigit = digit.toString()
                delay(1000.milliseconds)
                onscreenDigit = ""
                delay(300.milliseconds) // pause between each digit
            }
            state =
                GameState.Input // and then once its done showing the numbers, go to the input state of the game
        }
    }

    fun filterGuess(text: String) {
        input = text.filter { it.isDigit() }
            .take(answer.length) // make sure its only digits and not longer than our answer
    }

    fun submit() { // check attempt, log attempt and then set the stage to result
        if (state != GameState.Input) return

        val attempt = Attempts(answer, input)
        log.add(attempt)
        lastAttempt = attempt
        state = GameState.Result
    }

    fun reset() { // clear everything and send back to picking stage
        timer?.cancel() // stop making the digits show one by one on screen if youre gonna leave early
        input = ""
        onscreenDigit = ""
        state = GameState.Pick
    }

    fun getAttempts(): List<Attempts> = log.getAttempts()
    fun total(): Int = log.totalAttempts()
    fun correct(): Int = log.correctAttempts()
    fun accuracy(): Double = log.accuracy()
}