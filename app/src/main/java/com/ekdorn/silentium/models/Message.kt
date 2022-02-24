package com.ekdorn.silentium.models

import com.ekdorn.silentium.core.Myte


data class Message(val text: Myte, val date: Int, var read: Boolean, val author: Contact)
