package com.example.avnil_rapidrecall

import kotlin.random.Random

class SequenceMaker {
    fun generate(length: Int): String {
        require(length in 1..10) { "Length must be between 1 and 10" }
        return List(length) { Random.nextInt(10) }.joinToString("")
    }
}