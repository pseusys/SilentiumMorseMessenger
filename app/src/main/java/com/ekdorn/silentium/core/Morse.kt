package com.ekdorn.silentium.core

import android.content.Context
import com.ekdorn.silentium.R
import com.ekdorn.silentium.managers.PreferenceManager
import java.util.*
import kotlin.math.roundToInt


/**
 * The following table contains [international morse code](https://morsecode.world/international/morse.html).
 * Non-character values (as 'end of transmission' or 'change to wabun code') are skipped.
 *
 * Special characters:
 * SPACE = BiBit.END
 * ERROR = 0b1010101010101010
 */
object Morse {
    val SPACE = BiBit.END.atom.toLong()
    const val ERROR = 0b1010101010101010L

    private var current = MorseInterpreter("default")

    fun Context.morse() = morse(PreferenceManager[this].get(R.string.pref_morse_language_key, "default"))

    fun morse(locale: String = "default"): MorseInterpreter {
        if (current.language != locale) current = MorseInterpreter(locale)
        return current
    }

    /**
     * How to calculate Morse element length:
     *
     * Transmission speed (in words per minute) = S
     * Typical word is considered to have 50 units (PARIS)
     *
     * Unit (dot) length = 1.2 / S = U ms
     * Dash length = 3 * U ms
     * Gap (between letters) length = 3 * U ms
     * End (between words, aka space) length = 7 * U ms
     *
     * If Farnsworth timing is enabled:
     *
     * Character speed = S = C
     * Transmission speed = S < 18 ? 18 : S
     * Added space between elements = (60 * C - 37.2 * S) / (S * C) = A ms
     *
     * Unit (dot) length = 1.2 / C = U ms
     * Dash length = 3 * U ms
     * Gap (between letters) length = 3 * U + (3 * A) / 19 ms
     * End (between words, aka space) length = 7 * U + (7 * A) / 19 ms
     */
    fun getLength(speed: Int, farnsworth: Boolean, biBit: BiBit): Int {
        val unit = if ((speed < 18) && farnsworth) 66.66666666667 else 1200.0 / speed
        val added = if ((speed < 18) && farnsworth) 60 / speed - 2.06666666667 else 0.0
        return when (biBit) {
            BiBit.DIT -> unit
            BiBit.DAH -> unit * 3
            BiBit.GAP -> unit * 3 + added * 157.89473684
            BiBit.END -> unit * 7 + added * 368.42105263
        }.roundToInt()
    }
}

class MorseInterpreter(val language: String) {
    val ref: String
    val name: String
    val flag: String
    private val code: Map<Long, String>

    init {
        val locale = if (language == "default") Locale.getDefault() else Locale.forLanguageTag(language)
        val bundle = ResourceBundle.getBundle("morse", locale)
        ref = bundle.getString("ref")
        name = bundle.getString("name")
        flag = bundle.getString("flag")
        val letterKeys = bundle.keySet().minus(arrayOf("ref", "name", "flag"))
        code = letterKeys.associateBy({ it.toLong(2) }, { bundle.getString(it) })
    }

    fun codeData () = code.minus(Morse.SPACE).minus(Morse.ERROR).entries.toList()

    fun getString(l: Long) = code[l] ?: code[Morse.ERROR]

    fun getLong(str: String) = code.entries.find { it.value == str }?.key ?: Morse.ERROR
}
