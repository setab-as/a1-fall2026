package com.example.avnil_rapidrecall

class AttemptsLog {
    private val attempts = mutableListOf<Attempts>()
    fun add(attempt: Attempts) { // add a new attempt to the list
        attempts.add(attempt)
    }

    fun getAttempts(): List<Attempts> =
        attempts.reversed() // reversed so that the most recent attempts are at the top

    fun totalAttempts(): Int = attempts.size // total number of attempts
    fun correctAttempts(): Int = attempts.count { it.isAnswer } // number of correct attempts

    fun accuracy(): Double {
        if (totalAttempts() == 0) return 0.0 // keep at 0 percent when theres no attempts uet
        return correctAttempts() * 100.0 / totalAttempts()
    }

}