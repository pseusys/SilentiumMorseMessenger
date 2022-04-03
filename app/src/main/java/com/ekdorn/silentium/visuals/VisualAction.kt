package com.ekdorn.silentium.visuals

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat


class VisualAction(private val icon: Int, private val color: Int, private val iconTint: Int?, var views: IntRange, val callback: (Int) -> Unit) {
    lateinit var iconDrawable: Drawable
    var colorColor = 0
    var tintColor: Int? = null

    fun load(context: Context) {
        iconDrawable = ContextCompat.getDrawable(context, icon)!!
        colorColor = ContextCompat.getColor(context, color)
        tintColor = iconTint?.let { ContextCompat.getColor(context, it) }
    }
}
