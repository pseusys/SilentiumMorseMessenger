package com.ekdorn.silentium.managers

import android.content.Context
import android.content.SharedPreferences


object PreferenceManager {
    private const val PREFERENCES_FILE = "settings"

    const val DAH_LENGTH_KEY = "dah_length"
    const val GAP_LENGTH_KEY = "gap_length"
    const val END_LENGTH_KEY = "end_length"
    const val EOM_LENGTH_KEY = "eom_length"

    private val initial = mapOf(
        Pair(DAH_LENGTH_KEY, 500),
        Pair(GAP_LENGTH_KEY, 500),
        Pair(END_LENGTH_KEY, 1500),
        Pair(EOM_LENGTH_KEY, 2000)
    )

    operator fun get(context: Context): SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

    @Suppress("UNCHECKED_CAST")
    fun <K> SharedPreferences.get(key: String) = this.all.getOrDefault(key, initial[key]) as K
}
