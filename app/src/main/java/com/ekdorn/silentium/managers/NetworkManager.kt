package com.ekdorn.silentium.managers

import android.net.Uri
import kotlinx.coroutines.delay


object NetworkManager {
    suspend fun imageToUri(internal: Uri): Uri {
        delay(1000L)
        return Uri.EMPTY
    }
}
