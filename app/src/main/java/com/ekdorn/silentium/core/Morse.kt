package com.ekdorn.silentium.core

import android.content.Context
import com.ekdorn.silentium.R
import com.ekdorn.silentium.managers.PreferenceManager
import java.util.*


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

    lateinit var ref: String
    lateinit var name: String
    lateinit var flag: String
    private lateinit var code: Map<Long, String>

    // TODO: Refactor to Class.
    fun init(context: Context) = reload(PreferenceManager[context].get(R.string.pref_morse_language_key, "default"))

    fun reload(language: String) {
        val locale = if (language == "default") Locale.getDefault() else Locale.forLanguageTag(language)
        val bundle = ResourceBundle.getBundle("morse", locale)
        ref = bundle.getString("ref")
        name = bundle.getString("name")
        flag = bundle.getString("flag")
        val letterKeys = bundle.keySet().minus(arrayOf("ref", "name", "flag"))
        code = letterKeys.associateBy({ it.toLong(2) }, { bundle.getString(it) })
    }

    fun codeData () = code.minus(SPACE).minus(ERROR).entries.toList()

    fun getString(l: Long) = code[l] ?: code[ERROR]

    fun getLong(str: String) = code.entries.find { it.value == str }?.key ?: ERROR


    fun getLength(speed: Int, farnsworth: Boolean, biBit: BiBit): Int {
        val unit = if ((speed < 18) && farnsworth) 67 else 1200 / speed
        val totalDelay = if ((speed < 18) && farnsworth) 1080 - 37.2 * speed else 0.0
        return when (biBit) {
            BiBit.DIT -> unit
            BiBit.DAH -> unit * 3
            BiBit.GAP -> unit * 3 + (totalDelay * 0.15789473684).toInt()
            BiBit.END -> unit * 7 + (totalDelay * 0.36842105263).toInt()
        }
    }
}
