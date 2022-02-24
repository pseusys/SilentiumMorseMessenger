package com.ekdorn.silentium.models


data class Dialog(val contact: Contact, val lastMessage: Message, val unreadCount: Int)
