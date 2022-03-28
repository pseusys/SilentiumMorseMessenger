package com.ekdorn.silentium.managers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ekdorn.silentium.models.Contact
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*


object UserManager {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val me = MutableLiveData(constructContact())

    fun post(context: Context) {
        val contact = constructContact()
        me.postValue(contact)

        val dao = DatabaseManager[context].contactDAO()
        scope.launch { dao.add(contact) }
    }

    operator fun get(context: Context): LiveData<Contact> {
        if (me.value == null) post(context)
        return me
    }

    private fun constructContact(): Contact {
        if (!CryptoManager.keysSaved()) throw Exception("CryptoManager hasn't been initialized!")
        val user = Firebase.auth.currentUser!!
        val key = CryptoManager.getPublicKey()
        val contact = if (!user.phoneNumber.isNullOrBlank()) user.phoneNumber!!
        else if (!user.email.isNullOrBlank()) user.email!!
        else "[unknown source]"
        val online = user.metadata?.lastSignInTimestamp ?: -1
        val name = if (user.displayName.isNullOrBlank()) "Me" else user.displayName
        return Contact(user.uid, name, contact, Date(online), key, user.photoUrl)
    }

    fun reload(context: Context) = Firebase.auth.currentUser!!.reload().addOnCompleteListener {
        post(context)
    }
}
