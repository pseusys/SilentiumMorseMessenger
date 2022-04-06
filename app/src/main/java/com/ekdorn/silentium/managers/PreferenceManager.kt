package com.ekdorn.silentium.managers

import android.content.Context


object PreferenceManager {
    const val PREFERENCE_NAME = "settings"
    private var resolver: PreferenceResolver? = null

    operator fun get(context: Context): PreferenceResolver {
        if (resolver == null) resolver = PreferenceResolver(context)
        return resolver as PreferenceResolver
    }

    class PreferenceResolver internal constructor(context: Context) {
        @PublishedApi internal val prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        @PublishedApi internal val resource = context.resources

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T> get(key: Int, def: T): T {
            val id = resource.getString(key)
            return when (T::class) {
                Integer::class -> prefs.getInt(id, def as Int) as T
                String::class -> prefs.getString(id, def as String) as T
                Boolean::class -> prefs.getBoolean(id, def as Boolean) as T
                Float::class -> prefs.getFloat(id, def as Float) as T
                Long::class -> prefs.getLong(id, def as Long) as T
                Set::class -> prefs.getStringSet(id, def as Set<String>) as T
                else -> throw Exception("Type ${T::class} can not be read from Shared Preferences!")
            }
        }

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T> set(key: Int, value: T) {
            val id = resource.getString(key)
            val editor = prefs.edit()
            when (T::class) {
                Integer::class -> editor.putInt(id, value as Int)
                String::class -> editor.putString(id, value as String)
                Boolean::class -> editor.putBoolean(id, value as Boolean)
                Float::class -> editor.putFloat(id, value as Float)
                Long::class -> editor.putLong(id, value as Long)
                Set::class -> editor.putStringSet(id, value as Set<String>)
                else -> throw Exception("Type ${T::class} does not fit inside Shared Preferences!")
            }
            editor.apply()
        }
    }
}
