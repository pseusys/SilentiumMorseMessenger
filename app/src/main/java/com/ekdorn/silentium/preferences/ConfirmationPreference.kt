package com.ekdorn.silentium.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.ekdorn.silentium.R


class ConfirmationPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : DialogPreference(context, attrs, defStyleAttr) {
    constructor (context: Context, attrs: AttributeSet?): this(context, attrs, R.attr.preferenceStyle)
    constructor (context: Context): this(context, null)

    init {
        isPersistent = false
    }
}
