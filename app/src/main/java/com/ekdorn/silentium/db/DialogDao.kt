package com.ekdorn.silentium.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.ekdorn.silentium.models.Dialog


@Dao
@RewriteQueriesToDropUnusedColumns
interface DialogDao {
    @Query("SELECT * FROM dialogs")
    fun getAll(): LiveData<List<Dialog>>

    @Query("DELETE FROM messages WHERE contact_id = :id")
    fun delete(id: String)
}
