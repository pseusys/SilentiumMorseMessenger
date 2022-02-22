package com.ekdorn.silentium.mvs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dialogs Fragment"
    }
    val text: LiveData<String> = _text
}
