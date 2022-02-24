package com.ekdorn.silentium.managers

import com.ekdorn.silentium.models.Contact
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


object UserManager {
    val me: Contact

    init {
        val user = Firebase.auth.currentUser!!
        val contact = (user.phoneNumber ?: user.email) ?: "[unknown source]"
        val online = user.metadata?.lastSignInTimestamp ?: -1
        me = Contact(user.displayName, contact, online)
    }
}
