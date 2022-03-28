package com.ekdorn.silentium.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ekdorn.silentium.models.*


@Database(entities = [Contact::class, Message::class, Note::class], views = [Dialog::class], version = 1)
@TypeConverters(Converter::class)
abstract class Database : RoomDatabase() {
    abstract fun contactDAO(): ContactDao
    abstract fun messageDAO(): MessageDao
    abstract fun dialogDAO(): DialogDao
    abstract fun noteDAO(): NoteDao
}
