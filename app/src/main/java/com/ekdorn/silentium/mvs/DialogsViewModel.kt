package com.ekdorn.silentium.mvs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.models.Dialog
import com.ekdorn.silentium.models.Message


class DialogsViewModel : ViewModel() {
    private val _dialogs = MutableLiveData<List<Dialog>>(listOf())
    val dialogs: LiveData<List<Dialog>> = _dialogs

    fun getDialogs() {
        val me = Contact("Me", "+34569872309", 0)
        val contact1 = Contact("Duuuude", "+12348762356", 12345)
        val contact2 = Contact("Dude", "smtp@web.dot", 12)
        val contact3 = Contact("Duuuuuuuuuuuude", "name@gmail.com", 34568)

        val dialog1 = Dialog(contact1, listOf(
            Message("And we laughed, oh".lowercase().toMyteReadable(), 12345, true, me),
            Message("And we cried, oh".lowercase().toMyteReadable(), 12378, false, contact1),
            Message("And thought: 'oh, what a life'...".lowercase().toMyteReadable(), 12395, false, contact1)
        ))
        val dialog2 = Dialog(contact2, listOf(
            Message("And we cried, oh".lowercase().toMyteReadable(), 12345, true, me),
            Message("And we laughed, oh".lowercase().toMyteReadable(), 12378, true, contact2),
            Message("And said: 'oh, that a death'...".lowercase().toMyteReadable(), 12395, false, me)
        ))
        val dialog3 = Dialog(contact3, listOf(
            Message("What about".lowercase().toMyteReadable(), 12345, true, me),
            Message("Like".lowercase().toMyteReadable(), 12378, true, contact3),
            Message("Enough android for today?..".lowercase().toMyteReadable(), 12395, true, contact3)
        ))

        _dialogs.postValue(listOf(dialog1, dialog2, dialog3))
    }
}
