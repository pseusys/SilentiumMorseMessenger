package com.ekdorn.silentium.core

import com.ekdorn.silentium.split


/**
 * **Myte** (stands for Multiple bYTE) - data type, represented by byte array, contains an infinite integer.
 * Each byte contains BiBits in reversed order.
 */
typealias Myte = ByteArray

fun Myte.toLongs() = toBiBits().split(BiBit.END).mapIndexed { index, word ->
    if (word.isNotEmpty()) {
        val chars = word.split(BiBit.GAP).map { it.biBitsToLong() }.toMutableList()
        if (index > 0) chars.add(0, -1L)
        chars
    } else emptyList()
}.flatten()

fun List<Long>.longsToMyte() = map { it.toBiBits() }.flatten().biBitsToMyte()

fun Myte.toReadableString() = toLongs().fold("") { acc, l -> "${acc}${Morse.getString(l)}" }

fun String.toMyteReadable(): Myte { TODO() }

fun Myte.toBinaryString() = toBiBits().fold("") { acc, bb -> "${acc}${bb.atom.toString(2)}" }

fun String.toMyteBinary(): Myte { TODO() }

fun Myte.toMorseString() = toBiBits().fold("") { acc, bb -> "${acc}${bb.sign}" }

fun String.toMyteMorse(): Myte { TODO() }
