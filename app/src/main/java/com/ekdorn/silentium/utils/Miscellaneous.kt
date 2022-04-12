package com.ekdorn.silentium.utils

import kotlin.math.pow


infix fun Float.pow(x: Float) = pow(x)

fun <T> List<T>.split(item: T): List<List<T>> {
    val list = mutableListOf<List<T>>()
    var sublist = mutableListOf<T>()
    forEach {
        if (it == item) {
            list.add(sublist)
            sublist = mutableListOf()
        } else sublist.add(it)
    }
    list.add(sublist)
    return list
}

fun String.next(): String {
    if (isEmpty()) return "a"
    val lastZ = indexOfLast { it != 'z' }
    return if (lastZ == -1) this.plus('a')
    else this.replaceRange(lastZ, lastZ + 1, this[lastZ].inc().toString())
}
