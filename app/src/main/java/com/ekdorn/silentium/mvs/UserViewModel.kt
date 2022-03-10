package com.ekdorn.silentium.mvs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekdorn.silentium.models.Contact
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


// TODO: move to contacts view model, make 'constructContact' public for use in e.g. notifications
class UserViewModel : ViewModel() {
    private val _user = MutableLiveData<FirebaseUser>(Firebase.auth.currentUser)
    private val _me = MutableLiveData(constructContact(Firebase.auth.currentUser!!))

    val user: LiveData<FirebaseUser> = _user
    val me: LiveData<Contact> = _me

    private fun constructContact(user: FirebaseUser): Contact {
        val contact = if (!user.phoneNumber.isNullOrBlank()) user.phoneNumber!!
        else if (!user.email.isNullOrBlank()) user.email!!
        else "[unknown source]"
        val online = user.metadata?.lastSignInTimestamp ?: -1
        return Contact(user.displayName, contact, online)
    }

    fun reload() = Firebase.auth.currentUser!!.reload().addOnCompleteListener {
        _user.postValue(Firebase.auth.currentUser)
        _me.postValue(constructContact(Firebase.auth.currentUser!!))
    }
}
