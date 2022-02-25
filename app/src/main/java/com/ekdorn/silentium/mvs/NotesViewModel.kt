package com.ekdorn.silentium.mvs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.models.Note
import java.lang.System.currentTimeMillis


class NotesViewModel : ViewModel() {
    private val _notes = MutableLiveData<List<Note>>(emptyList())
    val notes: LiveData<List<Note>> = _notes

    fun saveNote(myte: Myte) {
        _notes.postValue(_notes.value!!.plus(Note(myte, currentTimeMillis())))
    }

    fun removeNote(index: Int) {
        _notes.postValue(_notes.value!!.filterIndexed { idx, _ -> idx != index })
    }

    fun sendNote(index: Int) {}
}
