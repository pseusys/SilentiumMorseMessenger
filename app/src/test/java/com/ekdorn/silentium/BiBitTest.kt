package com.ekdorn.silentium

import com.ekdorn.silentium.core.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test


class BiBitTest {
    private val longs = longArrayOf(0b0, 0b1011, 0b10, 0b11101011, 0b1111111111, 0b1111111010, 0b111111101010, 0b111011111011, 0b11111111, 0b1111101111, 0b1111101011)
    private val bibitsList = listOf(
        listOf(BiBit.END),
        listOf(BiBit.DIT, BiBit.DAH),
        listOf(BiBit.DIT),
        listOf(BiBit.DAH, BiBit.DIT, BiBit.DIT, BiBit.DAH),
        listOf(BiBit.DAH, BiBit.DAH, BiBit.DAH, BiBit.DAH, BiBit.DAH),
        listOf(BiBit.DAH, BiBit.DAH, BiBit.DAH, BiBit.DIT, BiBit.DIT),
        listOf(BiBit.DAH, BiBit.DAH, BiBit.DAH, BiBit.DIT, BiBit.DIT, BiBit.DIT),
        listOf(BiBit.DAH, BiBit.DIT, BiBit.DAH, BiBit.DAH, BiBit.DIT, BiBit.DAH),
        listOf(BiBit.DAH, BiBit.DAH, BiBit.DAH, BiBit.DAH),
        listOf(BiBit.DAH, BiBit.DAH, BiBit.DIT, BiBit.DAH, BiBit.DAH),
        listOf(BiBit.DAH, BiBit.DAH, BiBit.DIT, BiBit.DIT, BiBit.DAH)
    )

    private val myte = Myte("285301770989293741189098424")
    private val bibits = bibitsList.flatten()


    @Test
    fun longToBiBits_isCorrect() = longs.forEachIndexed { index, long -> assertThat(long.toBiBits(), `is`(bibitsList[index])) }

    @Test
    fun biBitsToLong_isCorrect() = bibitsList.forEachIndexed { index, bibit -> assertThat(bibit.biBitsToLong(), `is`(longs[index])) }


    @Test
    fun myteToBiBits_isCorrect() = assertThat(bibits.biBitsToMyte(), `is`(myte))

    @Test
    fun biBitsToMyte_isCorrect() = assertThat(myte.toBiBits(), `is`(bibits))
}
