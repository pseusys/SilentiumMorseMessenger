package com.ekdorn.silentium.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekdorn.silentium.models.Note


@Dao
@RewriteQueriesToDropUnusedColumns
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAll(): LiveData<List<Note>>

    @Insert
    fun add(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)
}
