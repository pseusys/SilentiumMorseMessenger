package com.ekdorn.silentium.managers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat


object ClipboardManager {
    class ClipboardHolder(val context: Context) {
        fun set(text: String, label: String = "Silentium clipboard contribution") {
            val manager = ContextCompat.getSystemService(context, ClipboardManager::class.java)!!
            manager.setPrimaryClip(ClipData.newPlainText(label, text))
            Toast.makeText(context, "Text copied!", Toast.LENGTH_SHORT).show()
        }
    }

    operator fun get(context: Context) = ClipboardHolder(context)
}
