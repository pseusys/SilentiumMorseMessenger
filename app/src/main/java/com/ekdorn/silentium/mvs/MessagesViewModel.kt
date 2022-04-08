package com.ekdorn.silentium.mvs

import android.app.Application
import androidx.lifecycle.*
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.managers.DatabaseManager
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.models.Message
import com.ekdorn.silentium.models.Payload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.lang.System.currentTimeMillis
import java.util.*


class MessagesViewModel(application: Application, private val contact: String) : AndroidViewModel(application) {
    private val daoScope = viewModelScope.plus(Dispatchers.IO)

    private val dao = DatabaseManager[application].messageDAO()
    var messages: LiveData<List<Message>> = dao.getFromContact(contact)

    fun addMessage(text: Myte, me: Contact, language: String) = daoScope.launch {
        dao.add(Message(Payload(text, language as CharSequence), Date(currentTimeMillis()), true, me.id, contact))
    }

    fun removeMessage(index: Int) = daoScope.launch {
        dao.delete(messages.value!![index])
    }
}

class MessageViewModelFactory(private val application: Application, private val contact: String): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == MessagesViewModel::class.java) {
            return MessagesViewModel(application, contact) as T
        } else throw Exception("The MessageViewModelFactory can instantiate MessagesViewModels only (not $modelClass)!")
    }
}
