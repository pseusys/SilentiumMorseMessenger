package com.ekdorn.silentium.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.ekdorn.silentium.R


class SeparatorView(context: Context, attributes: AttributeSet?, style: Int) : View(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.separator))
    }
}
