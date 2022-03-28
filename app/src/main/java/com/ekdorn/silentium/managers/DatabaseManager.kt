package com.ekdorn.silentium.managers

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.toHexString
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.db.Database


object DatabaseManager {
    private const val DB_NAME = "silent_db"
    private var db: Database? = null

    private fun prepopulate(context: Context, db: SupportSQLiteDatabase) {
        val insertReader = context.resources.openRawResource(R.raw.create).bufferedReader()
        while (insertReader.ready()) {
            var str = insertReader.readLine()
            str = str.replace("%1", "Glad you made it to Silentium!".toMyteReadable().toHexString())
            str = str.replace("%2", "For now this is still an alpha version.".toMyteReadable().toHexString())
            db.execSQL(str)
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
