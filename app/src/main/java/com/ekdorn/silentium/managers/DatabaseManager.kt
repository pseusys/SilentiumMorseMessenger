package com.ekdorn.silentium.managers

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ekdorn.silentium.R
import com.ekdorn.silentium.db.Database


object DatabaseManager {
    private const val DB_NAME = "silent_db"
    private var db: Database? = null

    operator fun get(context: Context) = if (db != null) db!! else {
        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                val insertReader = context.resources.openRawResource(R.raw.create).bufferedReader()
                while (insertReader.ready()) db.execSQL(insertReader.readLine())
                insertReader.close()
            }
        }

        Room.databaseBuilder(context, Database::class.java, DB_NAME)
            .enableMultiInstanceInvalidation()
            .addCallback(callback)
            .build().apply { db = this }
    }
}
