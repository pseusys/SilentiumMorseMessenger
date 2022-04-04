package com.ekdorn.silentium.views

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.ekdorn.silentium.databinding.ViewCombinedInputBinding


class CombinedInputView(context: Context, attributes: AttributeSet?, style: Int) : ConstraintLayout(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    private var silentDefault = false
    private val binding = ViewCombinedInputBinding.inflate(LayoutInflater.from(context), this, true)

    var text: Editable = binding.messageInput.text

    init {
        binding.keyboardSwitchButton.setOnClickListener {
            silentDefault = !silentDefault
            binding.messageInput.showSoftInputOnFocus = !silentDefault

            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (silentDefault) {
                imm?.hideSoftInputFromWindow(binding.messageInput.windowToken, 0)
                binding.keyboard.reset(true)
            } else {
                binding.keyboard.reset(false)
                imm?.showSoftInput(binding.messageInput, 0)
            }
        }

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
            true
        } else false
    } else false
}
