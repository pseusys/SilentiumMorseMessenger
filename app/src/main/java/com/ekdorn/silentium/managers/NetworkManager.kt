package com.ekdorn.silentium.managers

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import com.bumptech.glide.request.RequestOptions
import com.ekdorn.silentium.R
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.models.Payload
import com.ekdorn.silentium.utils.next
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.json.JSONObject
import java.security.PublicKey
import java.util.*
import android.util.Base64.*
import com.ekdorn.silentium.BuildConfig
import com.ekdorn.silentium.services.MessagingService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object NetworkManager {
    val options = RequestOptions.circleCropTransform().placeholder(R.drawable.picture_image).error(R.drawable.picture_imageless)

    private inline val reference: DatabaseReference
        get() = Firebase.database.getReference(Firebase.auth.currentUser!!.uid)


    suspend fun updateUser(time: Boolean = false, token: String? = null, name: String? = null, photo: String? = null, contact: Context? = null, key: PublicKey? = null) {
        if (time) suspendCoroutine<Void> { cont ->
            reference.child("last_online").setValue(Date().time).addOnCompleteListener { cont.resume(it.result) }
        }
        if ((token != null) || (MessagingService.lateToken != null)) suspendCoroutine<Void> { cont ->
            val tkn = token ?: MessagingService.lateToken
            MessagingService.lateToken = null
            reference.child("token").setValue(tkn).addOnCompleteListener { cont.resume(it.result) }
        }
        if (name != null) suspendCoroutine<Void> { cont ->
            reference.child("name").setValue(name).addOnCompleteListener { cont.resume(it.result) }
        }
        if (photo != null) suspendCoroutine<Void> { cont ->
            reference.child("photo").setValue(photo).addOnCompleteListener { cont.resume(it.result) }
        }
        if (contact != null) suspendCoroutine<Void> { cont ->
            reference.child("contact").setValue(UserManager[contact].value!!.contact).addOnCompleteListener { cont.resume(it.result) }
        }
        if (key != null) suspendCoroutine<Void> { cont ->
            reference.child("public_key").setValue(CryptoManager.publicKeyToString(key)).addOnCompleteListener { cont.resume(it.result) }
        }
    }

    suspend fun uploadProfileImage(internal: Uri): Uri {
        delay(1000L)
        return Uri.EMPTY
    }

    suspend fun logout(activity: Activity) = suspendCoroutine<Void> { cont ->
        AuthUI.getInstance().signOut(activity).addOnCompleteListener { cont.resume(it.result) }
    }

    suspend fun leave(activity: Activity) {
        suspendCoroutine<Void> { cont ->
            reference.removeValue().addOnCompleteListener { cont.resume(it.result) }
        }
        suspendCoroutine<Void> { cont ->
            AuthUI.getInstance().delete(activity).addOnCompleteListener { cont.resume(it.result) }
        }
    }


    suspend fun findContacts(contact: String): Map<String, String>? = suspendCoroutine { cont ->
        Firebase.database.reference.orderByChild("contact").startAt(contact).endAt(contact.next()).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cont.resume(snapshot.getValue<Map<String, Map<String, Any>>>()?.mapValues { it.value["contact"].toString() })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onCancelled: $error")
                }
            })
    }

    suspend fun updateContact(id: String): Contact = suspendCoroutine { cont ->
        Firebase.database.reference.child(id).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val received = snapshot.getValue<Map<String, Any>>()!!
                    cont.resume(Contact(
                        id,
                        received["name"].toString(),
                        received["contact"].toString(),
                        (received["last_online"] as? Long)?.let { Date(it) },
                        CryptoManager.stringToPublicKey(received["public_key"].toString()),
                        Uri.parse(received["photo"].toString()),
                        received["token"].toString()
                    ))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onCancelled: $error")
                }
            })
    }


    @Suppress("BlockingMethodInNonBlockingContext")
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun sendMessage(context: Context, contactId: String, message: Payload) = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val data = ProtoBuf.encodeToByteArray(message)

        val json = JSONObject()
        val notificationJson = JSONObject()
        val dataJson = JSONObject()
        notificationJson.put("body", encodeToString(data, DEFAULT))
        notificationJson.put("title", UserManager[context].value!!.name ?: UserManager[context].value!!.contact)
        notificationJson.put("priority", "high")
        dataJson.put("customId", "02")
        dataJson.put("fromId", contactId)
        json.put("notification", notificationJson)
        json.put("data", dataJson)
        json.put("to", DatabaseManager[context].contactDAO().findContact(contactId).token)

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        val request = Request.Builder()
            .header("Authorization", "key=${BuildConfig.WEB_KEY}")
            .url("https://fcm.googleapis.com/fcm/send")
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        return@withContext response.isSuccessful
    }
}
