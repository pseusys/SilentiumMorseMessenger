package com.ekdorn.silentium.models

import android.net.Uri
import androidx.room.*
import java.security.PublicKey
import java.util.*


@Entity(tableName = "contacts", indices = [Index(value = ["contact"], unique = true)])
data class Contact(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "contact") val contact: String,
    @ColumnInfo(name = "was_online") val wasOnline: Date?,
    @ColumnInfo(name = "public_key") val key: PublicKey?,
    @ColumnInfo(name = "profile_pic") val avatar: Uri?,
    @ColumnInfo(name = "token") val token: String?
)
