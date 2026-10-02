package com.example.avnil_rapidrecall

import kotlin.random.Random

/**
 * SequenceMaker
 *
 * Purpose: Make a random sequence of digits of a given length by the user.
 *
 * Design Rationale: Ensure that no lengths outside the range of 1-10 are allowed, and must keep
 * it as a string so that a sequence like "026" can be preserved and used in the game without that
 * leading zero being lost, as this would be lost if it were an integer and cause issues in the game.
 *
 * Outstanding Issues: None
 */
class SequenceMaker {
    fun generate(length: Int): String {
        require(length in 1..10) { "Length must be between 1 and 10!" } // reject lengths outside the range
        return List(length) { Random.nextInt(10) }.joinToString("")
    }
}