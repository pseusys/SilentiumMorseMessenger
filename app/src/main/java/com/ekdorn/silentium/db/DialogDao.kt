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

    //@Query("SELECT * FROM messages JOIN contacts ON contact_id = id")
    //fun delete(contact: String)
}
