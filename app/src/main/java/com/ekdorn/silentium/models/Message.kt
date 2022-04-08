package com.ekdorn.silentium.models

import androidx.room.*
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.utils.MyteSerializer
import kotlinx.serialization.Serializable
import java.util.*


enum class DataType(val type: String) {
    TEXT("text"), AUDIO("snd"), IMAGE("img")
}

@Serializable
data class Payload(
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.BLOB) @Serializable(with = MyteSerializer::class) val data: Myte,
    @ColumnInfo(name = "type") val type: String
) {
    constructor(text: Myte, morse: CharSequence): this(text, "${DataType.TEXT.type}/${morse}")
    constructor(data: ByteArray, type: DataType): this(Myte(1, data), type.type)
}


@Entity(tableName = "messages", indices = [Index(value = ["contact_id"])])
data class Message(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "inc") val id: Int,
    @Embedded(prefix = "load_") val payload: Payload,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "read") var read: Boolean,
    @ColumnInfo(name = "author_id") val authorID: String,
    @ColumnInfo(name = "contact_id") val contactID: String,
) {
    constructor(text: Payload, date: Date, read: Boolean, authorID: String, contactID: String): this(0, text, date, read, authorID, contactID)
}
