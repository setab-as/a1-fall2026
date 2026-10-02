package com.example.avnil_rapidrecall

class Attempts(
    val answer: String,
    val guess: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val length = answer.length
    val isAnswer = guess == answer
}