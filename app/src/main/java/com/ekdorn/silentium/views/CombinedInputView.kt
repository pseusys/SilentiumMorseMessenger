package com.ekdorn.silentium.views

import android.content.Context
import android.os.Build
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodSubtype
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.ekdorn.silentium.R
import com.ekdorn.silentium.databinding.ViewCombinedInputBinding
import com.ekdorn.silentium.managers.PreferenceManager
import java.util.*


class CombinedInputView(context: Context, attributes: AttributeSet?, style: Int) : ConstraintLayout(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    private var silentDefault = PreferenceManager[context].get(R.string.pref_keyboard_default_key, false)
    private val binding = ViewCombinedInputBinding.inflate(LayoutInflater.from(context), this, true)

    private var ims: InputMethodSubtype? = null

    val text: Editable
        get() = binding.messageInput.text

    val morse: String
        get() = if (silentDefault) binding.keyboard.morse.language else {
            val locale = ims?.language() ?: Locale.getDefault().language
            if (locale in binding.keyboard.locales) locale else "default"
        }

    @Suppress("DEPRECATION")
    private fun InputMethodSubtype.language(): String? {
        val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) languageTag else locale
        return tag.split('-').firstOrNull()
    }

    init {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        ims = imm?.currentInputMethodSubtype

        binding.keyboardSwitchButton.setOnClickListener {
            silentDefault = !silentDefault
            binding.messageInput.showSoftInputOnFocus = !silentDefault

            if (silentDefault) {
                imm?.hideSoftInputFromWindow(binding.messageInput.windowToken, 0)
                ims = null
                binding.keyboard.reset(true)
            } else {
                binding.keyboard.reset(false)
                ims = imm?.currentInputMethodSubtype
                imm?.showSoftInput(binding.messageInput, 0)
            }
        }

        binding.messageInput.showSoftInputOnFocus = !silentDefault
        binding.messageInput.setOnFocusChangeListener { _, focus -> if (silentDefault) binding.keyboard.reset(focus) }
    }

    override fun setOnClickListener(l: OnClickListener?) = binding.sendButton.setOnClickListener(l)

    private fun KeyboardView.reset(show: Boolean) {
        isVisible = show
        input = if (show) { { binding.messageInput.onCreateInputConnection(EditorInfo()) } } else null
    }

    fun onBackPressed(): Boolean = if (silentDefault) {
        if (binding.keyboard.isVisible) {
            binding.keyboard.reset(false)
            binding.messageInput.clearFocus()
            true
        } else false
    } else false
}
