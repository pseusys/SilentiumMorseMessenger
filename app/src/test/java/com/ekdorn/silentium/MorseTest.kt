package com.ekdorn.silentium

import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse
import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.math.roundToInt


class MorseTest {
    private val charLongs = listOf<Long>(0b10111110, 0b1010101111, 0b111111111111101011101111111111, 0b1011101011)
    private val spaceLong = 0b00L
    private val errorLong = 0b1010101010101010L
    private val errorousLong = 0b01010101010101010101101010101010101010101010110101L

    private val charStrings = listOf("P", "3", "%", "È")
    private val spaceString = " "
    private val errorString = "�"

    private val locales = arrayOf("default", "ru")
    private val flags = arrayOf("\uD83C\uDDFA\uD83C\uDDF3", "\uD83C\uDDF7\uD83C\uDDFA")

    private val transmissions = arrayOf(1, 6, 18, 38, 100)

    private val morse = Morse.morse()


    // Reference: http://www.arrl.org/files/file/Technology/x9004008.pdf
    @Test
    fun getLength_isCorrect() = transmissions.flatMap { listOf(it to true, it to false) }.forEach {
        val added = (60 * 18 - 37.2 * it.first) / (18 * it.first)
        val unit = if (it.second && (it.first < 18)) 1.2 / 18 else 1.2 / it.first
        assertEquals((unit * 1000).roundToInt(), Morse.getLength(it.first, it.second, BiBit.DIT))
        val dash = 3 * unit
        assertEquals((dash * 1000).roundToInt(), Morse.getLength(it.first, it.second, BiBit.DAH))
        val gap = 3 * unit + if (it.second && (it.first < 18)) (3 * added) / 19 else 0.0
        assertEquals((gap * 1000).roundToInt(), Morse.getLength(it.first, it.second, BiBit.GAP))
        val end = 7 * unit + if (it.second && (it.first < 18)) (7 * added) / 19 else 0.0
        assertEquals((end * 1000).roundToInt(), Morse.getLength(it.first, it.second, BiBit.END))
    }

    @Test
    fun morse_isCorrect() {
        locales.forEachIndexed { index, locale -> assertEquals(Morse.morse(locale).flag, flags[index]) }
        assertEquals(Morse.morse().hashCode(), Morse.morse().hashCode())
    }


    @Test
    fun getString_isCorrect() {
        charLongs.forEachIndexed { index, long -> assertEquals(charStrings[index], morse.getString(long)) }
        assertEquals(spaceString, morse.getString(spaceLong),)
        assertEquals(errorString, morse.getString(errorousLong))
        assertEquals(errorString, morse.getString(errorLong))
    }

    @Test
    fun getLong_isCorrect() {
        charStrings.forEachIndexed { index, string -> assertEquals(charLongs[index], morse.getLong(string)) }
        assertEquals(spaceLong, morse.getLong(spaceString))
        assertEquals(errorLong, morse.getLong(errorString))
    }
}
