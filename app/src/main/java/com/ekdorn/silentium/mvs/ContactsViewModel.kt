package com.ekdorn.silentium.mvs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekdorn.silentium.models.Contact
import java.lang.System.currentTimeMillis


class ContactsViewModel : ViewModel() {
    private val _internal = MutableLiveData<List<Contact>>(listOf())
    private val _external = MutableLiveData<List<Contact>>(listOf())

    val internal: LiveData<List<Contact>> = _internal
    val external: LiveData<List<Contact>> = _external

    fun addContact() {
        _internal.postValue(internal.value!!.plus(Contact(null, "+79213875621", currentTimeMillis())))
    }

    fun removeContact(index: Int) {
        _internal.postValue(_internal.value!!.filterIndexed { idx, _ -> idx != index })
    }

    fun syncContacts() {
        _external.postValue(listOf(
            Contact("Name", "email@website.ord", 124),
            Contact("Name1", "mail@website.ord", 125),
            Contact("Another name", "email@site.org", 1289)
        ))
    }
}
