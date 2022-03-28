package com.ekdorn.silentium.managers

import android.content.Context
import android.content.SharedPreferences
import com.ekdorn.silentium.utils.*


object PreferenceManager {
    private const val PREFERENCES_FILE = "settings"

    // Morse code preferences
    const val DAH_LENGTH_KEY = "dah_length"
    const val GAP_LENGTH_KEY = "gap_length"
    const val END_LENGTH_KEY = "end_length"
    const val EOM_LENGTH_KEY = "eom_length"

    @PublishedApi internal val initial = mapOf(
        Pair(DAH_LENGTH_KEY, 500L),
        Pair(GAP_LENGTH_KEY, 500L),
        Pair(END_LENGTH_KEY, 1500L),
        Pair(EOM_LENGTH_KEY, 2000L)
    )

    operator fun get(context: Context): SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> SharedPreferences.get(key: String) = when (T::class) {
        Integer::class -> getInt(key, initial[key] as Int) as T
        String::class -> getString(key, initial[key] as String) as T
        Boolean::class -> getBoolean(key, initial[key] as Boolean) as T
        Float::class -> getFloat(key, initial[key] as Float) as T
        Long::class -> getLong(key, initial[key] as Long) as T
        Set::class -> getStringSet(key, initial[key] as Set<String>) as T
        else -> throw Exception("Type ${T::class} can not be read Shared Preferences!")
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> SharedPreferences.getLive(key: String) = when (T::class) {
        Integer::class -> SharedPreferenceIntLiveData(this, key, initial[key] as Int)
        String::class -> SharedPreferenceStringLiveData(this, key, initial[key] as String)
        Boolean::class -> SharedPreferenceBooleanLiveData(this, key, initial[key] as Boolean)
        Float::class -> SharedPreferenceFloatLiveData(this, key, initial[key] as Float)
        Long::class -> SharedPreferenceLongLiveData(this, key, initial[key] as Long)
        Set::class -> SharedPreferenceStringSetLiveData(this, key, initial[key] as Set<String>)
        else -> throw Exception("Live type ${T::class} can not exist in Shared Preferences!")
    } as SharedPreferenceLiveData<T>

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> SharedPreferences.set(key: String, value: T) {
        val editor = edit()
        when (T::class) {
            Integer::class -> editor.putInt(key, value as Int)
            String::class -> editor.putString(key, value as String)
            Boolean::class -> editor.putBoolean(key, value as Boolean)
            Float::class -> editor.putFloat(key, value as Float)
            Long::class -> editor.putLong(key, value as Long)
            Set::class -> editor.putStringSet(key, value as Set<String>)
            else -> throw Exception("Type ${T::class} does not fit inside Shared Preferences!")
        }
        editor.apply()
    }
}
