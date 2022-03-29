package com.ekdorn.silentium.models

import androidx.room.*


@DatabaseView("SELECT *, SUM(NOT read) AS unread FROM (SELECT * FROM messages JOIN contacts ON contact_id = id ORDER BY date DESC) GROUP BY id", viewName = "dialogs")
data class Dialog(
    @Embedded val contact: Contact,
    @Embedded val lastMessage: Message,
    @ColumnInfo(name = "unread") val unreadCount: Int,
)
