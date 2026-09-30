package com.example.avnil_rapidrecall

import android.R.id.input
import java.time.LocalDateTime

class Attempts(
    val answer: String,
    val guess: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val length = answer.length
    val isAnswer = guess == answer
}