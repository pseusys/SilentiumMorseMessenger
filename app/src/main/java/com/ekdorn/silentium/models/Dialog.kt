package com.ekdorn.silentium.models

import androidx.room.*


@DatabaseView("SELECT *, COUNT(CASE read WHEN 'false' THEN 1 ELSE null END) AS unread FROM (SELECT * FROM messages JOIN contacts ON contact_id = id ORDER BY date DESC) GROUP BY id", viewName = "dialogs")
data class Dialog(
    @Embedded val contact: Contact,
    @Embedded val lastMessage: Message,
    @ColumnInfo(name = "unread") val unreadCount: Int,
)
