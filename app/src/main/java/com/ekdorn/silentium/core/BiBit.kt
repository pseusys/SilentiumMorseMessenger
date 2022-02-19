package com.ekdorn.silentium.core


/**
 * **BiBit** (stands for "BInary BIT") - data type, represented by 2 bits, can have 4 different states.
 * These states are:
 * - DIT (10) - Morse code "dot", short signal
 * - DAH (11) - Morse code "dash", long signal
 * - GAP (01) - pause between characters (as Morse code is not an infix nor postfix code)
 * - END (00) - whitespace, pause between words
 * TODO: add Morse code vs ASCII letter size comparison
 */
enum class BiBit (val atom: Byte, val sign: Char) {
    DIT(0b10, '.'), DAH(0b11, '-'), GAP(0b01, ' '), END(0b00, '\t');

    companion object {
        fun fromAtom(atom: Byte) = values().associateBy(BiBit::atom)[atom]!!
    }
}

fun Long.toBiBits(): List<BiBit> {
    val biBits = mutableListOf<BiBit>()
    var intermediate = this
    do {
        biBits.add(0, BiBit.fromAtom((intermediate and 0b11).toByte()))
        intermediate = intermediate shr 2
    } while ((intermediate and 0b11) > 0)
    return biBits
}

fun Long.toMorseString() = toBiBits().fold("") { acc, bb -> "${acc}${bb.sign}" }

fun List<BiBit>.biBitsToLong() = fold(0L) { acc, biBit -> (acc shl 2) + biBit.atom }

fun Myte.toBiBits(): List<BiBit> {
    val biBits = mutableListOf<BiBit>()
    forEach { for (i in 0 .. 3) biBits.add(BiBit.fromAtom(((it.toInt() shr i * 2) and 0b11).toByte())) }
    while (biBits.lastOrNull() == BiBit.END) biBits.removeLast()
    return biBits
}

fun List<BiBit>.biBitsToMyte() = chunked(4).map { byte ->
    byte.withIndex().sumOf { it.value.atom.toInt() shl (it.index * 2) }.toByte()
}.toByteArray()
