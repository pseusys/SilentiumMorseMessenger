package com.ekdorn.silentium

import com.ekdorn.silentium.utils.split
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test


class MiscellaneousTest {
    private val list = listOf(2, 4, 5, 12, -1, 4, 7, 8, -1, -1, 34)
    private val splitted = listOf(listOf(2, 4, 5, 12), listOf(4, 7, 8), listOf(), listOf(34))


    @Test
    fun split_isCorrect() = assertThat(list.split(-1), `is`(splitted))
}
