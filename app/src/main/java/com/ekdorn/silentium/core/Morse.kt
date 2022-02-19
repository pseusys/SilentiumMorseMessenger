package com.ekdorn.silentium.core

import java.util.*


/**
 * The following table contains [international morse code](https://morsecode.world/international/morse.html).
 * Non-character values (as 'end of transmission' or 'change to wabun code') are skipped.
 */
object Morse {
    val DAH_LENGTH_KEY = Pair("DAH_LENGTH", 500)
    val GAP_LENGTH_KEY = Pair("GAP_LENGTH", 500)
    val END_LENGTH_KEY = Pair("END_LENGTH", 1000)
    val EOM_LENGTH_KEY = Pair("EOM_LENGTH", 2000)

    val ref: String
    val name: String
    private val code: Map<Long, String>

    init {
        val bundle = ResourceBundle.getBundle("morse")
        ref = bundle.getString("ref")
        name = bundle.getString("name")
        val letterKeys = bundle.keySet().minus(arrayOf("ref", "name"))
        code = letterKeys.associateBy({ it.toLong(2) }, { bundle.getString(it) }).plus(Pair(-1L, " "))
    }

    fun codeData () = code.minus(-1L).entries.toList()

    fun getString(l: Long) = if (code.containsKey(l)) code[l]!! else "?"
}
