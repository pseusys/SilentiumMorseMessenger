package com.ekdorn.silentium.mvs

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.core.toReadableString
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

    fun sendNote(context: Context, index: Int) = daoScope.launch {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, notes.value!![index].text.toReadableString(context))
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}
