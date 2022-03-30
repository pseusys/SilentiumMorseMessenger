package com.ekdorn.silentium

import com.ekdorn.silentium.core.Morse
import junit.framework.TestCase.assertEquals
import org.junit.Test


class MorseTest {
    private val charLongs = listOf<Long>(0b10111110, 0b1010101111, 0b111111111111101011101111111111, 0b1011101011)
    private val spaceLong = 0b00L
    private val errorLong = 0b1010101010101010L
    private val errorousLong = 0b01010101010101010101101010101010101010101010110101L

    private val charStrings = listOf("P", "3", "%", "È")
    private val spaceString = " "
    private val errorString = "�"


    @Test
    fun getString_isCorrect() {
        charLongs.forEachIndexed { index, long -> assertEquals(Morse.getString(long), charStrings[index]) }
        assertEquals(Morse.getString(spaceLong), spaceString)
        assertEquals(Morse.getString(errorousLong), errorString)
        assertEquals(Morse.getString(errorLong), errorString)
    }

    @Test
    fun getLong_isCorrect() {
        charStrings.forEachIndexed { index, string -> assertEquals(Morse.getLong(string), charLongs[index]) }
        assertEquals(Morse.getLong(spaceString), spaceLong)
        assertEquals(Morse.getLong(errorString), errorLong)
    }


    @Test
    fun locale_isCorrect() {
        // TODO: Not yet implemented!
    }
}
