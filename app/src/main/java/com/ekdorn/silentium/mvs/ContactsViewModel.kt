package com.ekdorn.silentium.mvs

import android.app.Application
import androidx.lifecycle.*
import com.ekdorn.silentium.managers.DatabaseManager
import com.ekdorn.silentium.models.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.lang.System.currentTimeMillis
import java.time.Instant
import java.util.*


class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val daoScope = viewModelScope.plus(Dispatchers.IO)

    private val dao = DatabaseManager[application].contactDAO()
    private val _external = MutableLiveData<List<Contact>>(listOf())

    val internal: LiveData<List<Contact>> = dao.getAll()
    val external: LiveData<List<Contact>> = _external

    fun addContact(contact: Contact) = daoScope.launch {
        dao.add(contact)
    }

    fun removeContact(index: Int) = daoScope.launch {
        dao.delete(internal.value!![index])
    }
}
