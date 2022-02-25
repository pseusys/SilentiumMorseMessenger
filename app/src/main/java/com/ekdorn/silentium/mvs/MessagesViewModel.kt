package com.ekdorn.silentium.mvs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.models.Message
import java.lang.System.currentTimeMillis


class MessagesViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>(listOf())
    val messages: LiveData<List<Message>> = _messages

    fun getMessages() {
        val me = UserManager.me
        val contact1 = Contact("Duuuude", "+12348762356", 12345)
        val contact3 = Contact("Duuuuuuuuuuuude", "name@gmail.com", 34568)

        val message1 = Message("I was thinking".lowercase().toMyteReadable(), 12395, false, contact1)
        val message2 = Message("Uhhhh...".lowercase().toMyteReadable(), 12395, false, me)
        val message3 = Message("Enough android for today, maybe?..".lowercase().toMyteReadable(), 12395, true, contact3)

        _messages.postValue(listOf(message1, message2, message3))
    }

    fun addMessage(text: Myte) {
        _messages.postValue(_messages.value!!.plus(Message(text, currentTimeMillis(), false, UserManager.me)))
    }

    fun removeMessage(index: Int) {
        _messages.postValue(_messages.value!!.filterIndexed { idx, _ -> idx != index })
    }
}
