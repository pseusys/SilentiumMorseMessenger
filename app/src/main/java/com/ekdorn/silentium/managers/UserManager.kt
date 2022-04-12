package com.ekdorn.silentium.managers

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceDataStore
import com.ekdorn.silentium.R
import com.ekdorn.silentium.models.Contact
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*


object UserManager {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val me = MutableLiveData(constructContact())

    private fun post(context: Context) {
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
        if (Firebase.auth.currentUser == null) throw Exception("UserManager requires logged in user!!")
        val user = Firebase.auth.currentUser!!
        val key = CryptoManager.getPublicKey()
        val contact = if (!user.phoneNumber.isNullOrBlank()) user.phoneNumber!!
        else if (!user.email.isNullOrBlank()) user.email!!
        else "[unknown source]"
        val online = user.metadata?.lastSignInTimestamp ?: -1
        val name = if (user.displayName.isNullOrBlank()) contact else user.displayName
        return Contact(user.uid, name, contact, Date(online), key, user.photoUrl, null)
    }


    class UserDataStore(private val context: Context): PreferenceDataStore() {
        private val userNameKey = context.resources.getString(R.string.pref_account_name_key)
        private val userPictureKey = context.resources.getString(R.string.pref_account_picture_key)

        override fun putString(key: String, value: String?) {
            scope.launch {
                val request = UserProfileChangeRequest.Builder()
                when (key) {
                    userNameKey -> {
                        request.displayName = value
                        NetworkManager.updateUser(name = value)
                    }
                    userPictureKey -> {
                        request.photoUri = NetworkManager.uploadProfileImage(Uri.parse(value))
                        NetworkManager.updateUser(photo = value)
                    }
                    else -> throw Error("User does not have '$key' parameter!")
                }
                Firebase.auth.currentUser!!.updateProfile(request.build()).addOnCompleteListener {
                    if (it.isSuccessful) post(context) else throw Error("Update request wasn't completed:\n${it.exception}")
                }
            }.invokeOnCompletion {
                if (it != null) Toast.makeText(context, "Could not connect to server!!", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getString(key: String, defValue: String?): String? {
            val user = Firebase.auth.currentUser!!
            return when (key) {
                userNameKey -> user.displayName
                userPictureKey -> user.photoUrl.toString()
                else -> throw Error("User does not have '$key' parameter!")
            }
        }
    }
}
