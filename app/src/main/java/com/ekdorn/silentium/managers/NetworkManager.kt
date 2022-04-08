package com.ekdorn.silentium.managers

import android.net.Uri
import android.util.Base64
import android.util.Log
import com.bumptech.glide.request.RequestOptions
import com.ekdorn.silentium.R
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.models.Payload
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.security.PublicKey
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object NetworkManager {
    val options = RequestOptions.circleCropTransform().placeholder(R.drawable.picture_image).error(R.drawable.picture_imageless)

    private val reference = Firebase.database.getReference(Firebase.auth.currentUser!!.uid)


    suspend fun publishUserKey(key: PublicKey) = suspendCoroutine<Void> { cont ->
        reference.child("public_key").setValue(Base64.encodeToString(key.encoded, 0)).addOnCompleteListener { cont.resume(it.result) }
    }

    suspend fun publishUserToken(token: Any) {

    }

    suspend fun uploadProfileImage(internal: Uri): Uri {
        delay(1000L)
        return Uri.EMPTY
    }

    suspend fun deleteUser() = suspendCoroutine<Void> { cont ->
        reference.removeValue().addOnCompleteListener { cont.resume(it.result) }
    }

/*
    suspend fun findContact(contact: String): Contact {

    }

    suspend fun updateContact(id: String): Contact {

    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun sendMessage(contactId: String, message: Payload) {
        val data = ProtoBuf.encodeToByteArray(message)
    }
 */
}
