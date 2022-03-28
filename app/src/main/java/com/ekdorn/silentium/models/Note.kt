package com.ekdorn.silentium.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ekdorn.silentium.core.Myte
import java.util.*


@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "inc") val id: Int,
    @ColumnInfo(name = "text", typeAffinity = ColumnInfo.BLOB) val text: Myte,
    @ColumnInfo(name = "date") val date: Date,
) {
    constructor(text: Myte, date: Date): this(0, text, date)
}
