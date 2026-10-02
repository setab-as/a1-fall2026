package com.example.avnil_rapidrecall

/**
 * Attempts
 *
 * Purpose: Store one finised attempt of the game, including the answer, guess, and timestamp.
 *
 * Design Rationale: Once you make your attempt, that attempt data cannot be changed. Things like the
 * sequences length and its correctness are derived from the answer and the guess so these will always
 * be correct no matter what.
 *
 * Outstanding Issues: None
 */
data class Attempts(
    val answer: String,
    val guess: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val length = answer.length
    val isAnswer = guess == answer
}