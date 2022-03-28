package com.ekdorn.silentium.mvs

import android.app.Application
import androidx.lifecycle.*
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.managers.DatabaseManager
import com.ekdorn.silentium.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.lang.System.currentTimeMillis
import java.util.*


class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val daoScope = viewModelScope.plus(Dispatchers.IO)

    private val dao = DatabaseManager[application].noteDAO()
    val notes: LiveData<List<Note>> = dao.getAll()

    fun saveNote(myte: Myte) = daoScope.launch {
        dao.add(Note(myte, Date(currentTimeMillis())))
    }

    fun removeNote(index: Int) = daoScope.launch {
        dao.delete(notes.value!![index])
    }

    fun sendNote(index: Int) {}
}
