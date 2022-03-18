package com.ekdorn.silentium.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ekdorn.silentium.models.Contact


@Dao
@RewriteQueriesToDropUnusedColumns
interface ContactDao {
    @Query("SELECT * FROM contacts")
    fun getAll(): LiveData<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id LIKE :id LIMIT 1")
    fun findContact(id: String): Contact

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(contact: Contact)

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)
}
