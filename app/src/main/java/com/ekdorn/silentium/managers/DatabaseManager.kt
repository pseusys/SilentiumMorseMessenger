package com.ekdorn.silentium.managers

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.db.Database


object DatabaseManager {
    private const val DB_NAME = "silent_db"
    private var db: Database? = null

    private fun prepopulate(context: Context, db: SupportSQLiteDatabase) {
        var paramPointer = 0
        val params = arrayOf(
            "GLAD YOU MADE IT TO SILENTIUM!".toMyteReadable(),
            "FOR NOW THIS IS STILL AN ALPHA VERSION...".toMyteReadable()
        )

        val insertReader = context.resources.openRawResource(R.raw.create).bufferedReader()
        while (insertReader.ready()) {
            val str = insertReader.readLine()
            val paramsNum = str.count { it == '?' }
            if (paramsNum > 0) {
                db.execSQL(str, params.slice(IntRange(paramPointer, paramPointer + paramsNum - 1)).toTypedArray())
                paramPointer += paramsNum
            } else db.execSQL(str)
        }
        insertReader.close()
    }

    operator fun get(context: Context) = if (db != null) db!! else {
        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) = prepopulate(context, db)
        }

        Room.databaseBuilder(context, Database::class.java, DB_NAME)
            .enableMultiInstanceInvalidation()
            .addCallback(callback)
            .build().apply { db = this }
    }
}
