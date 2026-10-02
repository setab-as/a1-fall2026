package com.example.avnil_rapidrecall

/**
 * AttemptsLog
 *
 * Purpose: Hold all the attempts made in the current session of the game and then provide the
 * total number of attempts, the number of correct attempts, and the accuracy of the game. Also makes
 * sure that the most recent attempts are at the top.
 *
 * Design Rationale: The other classes can add attempts but can not delete or edit their data
 * under any circumstances, we are handing out the attempts in the order of most recent first,
 * and only as a copy so the original is kept safe. Also when there are no attempts the accuracy
 * immediately returns 0 so that we do not end up dividing by 0.
 *
 * Outstanding Issues: None
 */
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