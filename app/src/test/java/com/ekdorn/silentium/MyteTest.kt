package com.ekdorn.silentium

import com.ekdorn.silentium.core.*
import junit.framework.TestCase.assertEquals
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.math.BigInteger


class MyteTest {
    private val readableString = "THE QUICK BROWN FOX JUMPS OVER LAZY DOG"
    private val binaryString = "0b110110101010011000111110110110101101101001111011100111101100111010100110111001111111011011110111100010101110011111110111101011001011111101101011011111011011111001101010001111110110101011011001101110001011101001101101111110100111101111001110100111111101111110"
    private val hexString = "0x7aa9cb7a7ade6b3ba9bdf9fd2abdfda3ef9ed7eb9acf9a76e2ea97fade3b6f7f2"
    private val morseString = "- .... .\t--.- ..- .. -.-. -.-\t-... .-. --- .-- -.\t..-. --- -..-\t.--- ..- -- .--. ...\t--- ...- . .-.\t.-.. .- --.. -.--\t-.. --- --."

    private val myte = Myte("343741338273217814399414800141895123778762081846105108012481114039899188402855")


    @Test
    fun readableToMyte_isCorrect() = assertThat(readableString.toMyteReadable("default"), `is`(myte))

    @Test
    fun binaryToMyte_isCorrect() = assertThat(binaryString.toMyteBinary(), `is`(myte))

    @Test
    fun hexToMyte_isCorrect() = assertThat(hexString.toMyteHex(), `is`(myte))

    @Test
    fun morseToMyte_isCorrect() = assertThat(morseString.toMyteMorse(), `is`(myte))


    @Test
    fun myteToReadable_isCorrect() = assertEquals(readableString, myte.toReadableString("default"))

    @Test
    fun myteToBinary_isCorrect() = assertEquals(binaryString, myte.toBinaryString())

    @Test
    fun myteToHex_isCorrect() = assertEquals(hexString, myte.toHexString())

    @Test
    fun myteToMorse_isCorrect() = assertEquals(morseString, myte.toMorseString())
}
