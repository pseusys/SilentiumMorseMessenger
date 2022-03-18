package com.ekdorn.silentium.db

import android.net.Uri
import androidx.room.TypeConverter
import com.ekdorn.silentium.managers.CryptoManager
import java.security.PublicKey
import java.util.*


class Converter {
    @TypeConverter
    fun toDate(timestamp: Long?) = timestamp?.let { Date(it) }

    @TypeConverter
    fun fromDate(date: Date?) = date?.time

    @TypeConverter
    fun toUri(string: String?) = string?.let { Uri.parse(it) }

    @TypeConverter
    fun fromUri(uri: Uri?) = uri?.toString()

    @TypeConverter
    fun toPublicKey(string: String?) = string?.let { CryptoManager.stringToPublicKey(it) }

    @TypeConverter
    fun fromPublicKey(key: PublicKey?) = key?.let { CryptoManager.publicKeyToString(it) }
}
