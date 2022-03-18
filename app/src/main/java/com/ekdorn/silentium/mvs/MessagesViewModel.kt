package com.ekdorn.silentium.mvs

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.managers.DatabaseManager
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.models.Dialog
import com.ekdorn.silentium.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.lang.System.currentTimeMillis
import java.util.*


class MessagesViewModel(application: Application) : AndroidViewModel(application) {
    private val daoScope = viewModelScope.plus(Dispatchers.IO)

    private val dao = DatabaseManager[application].messageDAO()
    private lateinit var cont: Contact
    lateinit var messages: LiveData<List<Message>>
        private set

    fun initContact(contact: Contact) = apply {
        cont = contact
        messages = dao.getFromContact(contact.id)
    }

    fun addMessage(text: Myte, me: Contact) = daoScope.launch {
        dao.add(Message(UUID.randomUUID().hashCode(), text, Date(currentTimeMillis()), true, me.id, cont.id))
    }

    fun removeMessage(index: Int) = daoScope.launch {
        dao.delete(messages.value!![index])
    }
}
