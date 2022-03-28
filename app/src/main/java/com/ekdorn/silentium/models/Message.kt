package com.ekdorn.silentium.models

import androidx.room.*
import com.ekdorn.silentium.core.Myte
import java.util.*


@Entity(tableName = "messages", indices = [Index(value = ["contact_id"])])
data class Message(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "inc") val id: Int,
    @ColumnInfo(name = "text", typeAffinity = ColumnInfo.BLOB) val text: Myte,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "read") var read: Boolean,
    @ColumnInfo(name = "author_id") val authorID: String,
    @ColumnInfo(name = "contact_id") val contactID: String,
) {
    constructor(text: Myte, date: Date, read: Boolean, authorID: String, contactID: String): this(0, text, date, read, authorID, contactID)
}
