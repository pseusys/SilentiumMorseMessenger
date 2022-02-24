package com.ekdorn.silentium.core

import java.util.*


/**
 * The following table contains [international morse code](https://morsecode.world/international/morse.html).
 * Non-character values (as 'end of transmission' or 'change to wabun code') are skipped.
 *
 * Special characters:
 * SPACE = 0b0
 * ERROR = 0b1010101010101010
 */
object Morse {
    val DAH_LENGTH_KEY = Pair("DAH_LENGTH", 500)
    val GAP_LENGTH_KEY = Pair("GAP_LENGTH", 500)
    val END_LENGTH_KEY = Pair("END_LENGTH", 1500)
    val EOM_LENGTH_KEY = Pair("EOM_LENGTH", 2500)

    private val SPACE = BiBit.END.atom.toLong()
    private const val ERROR = 0b1010101010101010L

    val ref: String
    val name: String
    private val code: Map<Long, String>

    init {
        val bundle = ResourceBundle.getBundle("morse")
        ref = bundle.getString("ref")
        name = bundle.getString("name")
        val letterKeys = bundle.keySet().minus(arrayOf("ref", "name"))
        code = letterKeys.associateBy({ it.toLong(2) }, { bundle.getString(it) }).plus(Pair(SPACE, " "))
    }

    fun codeData () = code.minus(SPACE).entries.toList()

    fun getString(l: Long) = code[l] ?: code[ERROR]

    fun getLong(str: String) = code.entries.find { it.value == str }?.key ?: ERROR
}
