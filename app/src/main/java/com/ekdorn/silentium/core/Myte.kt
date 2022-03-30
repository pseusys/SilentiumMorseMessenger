package com.ekdorn.silentium.core

import com.ekdorn.silentium.utils.split


/**
 * **Myte** (stands for Multiple bYTE) - data type, represented by byte array, contains an infinite integer.
 * Each byte contains BiBits in reversed order.
 */
typealias Myte = ByteArray

fun Myte.toLongs(): List<Long> {
    val biBits = toBiBits().split(BiBit.END)
    return if ((biBits.size == 1) && biBits.single().isEmpty()) listOf(0)
    else biBits.mapIndexed { index, word ->
        val chars = if (word.isNotEmpty()) word.split(BiBit.GAP).map { it.biBitsToLong() }.toMutableList()
        else mutableListOf()
        if (index > 0) chars.add(0, BiBit.END.atom.toLong())
        chars
    }.flatten()
}

// TODO: refactor this and BiBit class methods for optimization
fun List<Long>.longsToMyte(): Myte {
    val biBits = map { it.toBiBits() }
    return biBits.mapIndexed { index, list ->
        if ((index == biBits.size - 1) || (list[0] == BiBit.END) || (biBits[index + 1][0] == BiBit.END)) list
        else list.plus(BiBit.GAP)
    }.flatten().biBitsToMyte()
}

fun Myte.toReadableString() = toLongs().fold("") { acc, l -> "${acc}${Morse.getString(l)}" }

fun String.toMyteReadable() = map { Morse.getLong(it.toString()) }.longsToMyte()

fun Myte.toBinaryString() = toBiBits().fold("0b") { acc, bb -> "${acc}${bb.atom.toString(2).padStart(2, '0')}" }

fun String.toMyteBinary() = substring(2).chunked(2).map { BiBit.fromAtom(it.toByte(2)) }.biBitsToMyte()

fun Myte.toHexString() = toBiBits().chunked(2).fold("0x") { acc, bb -> "${acc}${bb.withIndex().sumOf { it.value.atom.toInt() shl (it.index * 2) }.toString(16)}" }

fun String.toMyteHex() = substring(2).flatMap { ch -> ch.digitToInt(16).let { listOf(it and 0b11, (it shr 2) and 0b11) }.map { BiBit.fromAtom(it.toByte()) } }.biBitsToMyte()

fun Myte.toMorseString() = toBiBits().fold("") { acc, bb -> "${acc}${bb.sign}" }

fun String.toMyteMorse() = map { BiBit.fromSign(it) }.biBitsToMyte()
