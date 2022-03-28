package com.ekdorn.silentium.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekdorn.silentium.models.Message


@Dao
@RewriteQueriesToDropUnusedColumns
interface MessageDao {
    @Query("SELECT * FROM messages JOIN contacts ON contact_id = id WHERE id LIKE :id ORDER BY date")
    fun getFromContact(id: String): LiveData<List<Message>>

    @Insert
    fun add(message: Message)

    @Update
    fun update(message: Message)

    @Delete
    fun delete(message: Message)
}
