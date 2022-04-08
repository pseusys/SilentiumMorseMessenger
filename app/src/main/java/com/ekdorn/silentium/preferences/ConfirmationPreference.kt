package com.ekdorn.silentium.preferences

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
import com.ekdorn.silentium.R


class ConfirmationPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : DialogPreference(context, attrs, defStyleAttr) {
    constructor (context: Context, attrs: AttributeSet?): this(context, attrs, R.attr.preferenceStyle)
    constructor (context: Context): this(context, null)

    var onClickListener: ((Boolean) -> Unit)? = null

    init {
        isPersistent = false
        dialogMessage = "Is your intention to ${dialogTitle.toString().lowercase()} strong enough?"
        positiveButtonText = "It is!"
        negativeButtonText = "Wait a minute..."
    }
    
    fun setConfirmationListener(listener: (Boolean) -> Unit) {
        onClickListener = listener
    }
}


class ConfirmationPreferenceDialogFragment(private val listener: ((Boolean) -> Unit)?) : PreferenceDialogFragmentCompat() {
    companion object {
        fun newInstance(preference: ConfirmationPreference) = ConfirmationPreferenceDialogFragment(preference.onClickListener).also {
            it.arguments = Bundle(1).apply { putString(ARG_KEY, preference.key) }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) = listener?.invoke(positiveResult) ?: Unit
}
