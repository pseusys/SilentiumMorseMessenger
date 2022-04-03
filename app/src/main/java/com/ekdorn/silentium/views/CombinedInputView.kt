package com.ekdorn.silentium.views

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.ekdorn.silentium.databinding.ViewCombinedInputBinding


class CombinedInputView(context: Context, attributes: AttributeSet?, style: Int) : ConstraintLayout(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    private var silentDefault = false
    private val binding = ViewCombinedInputBinding.inflate(LayoutInflater.from(context), this, true)

    private var listener: OnClickListener? = null
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

        binding.messageInput.setOnEditorActionListener { _, actionID, keyEvent ->
            if ((actionID == EditorInfo.IME_ACTION_SEND) || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                listener?.onClick(binding.messageInput)
                true
            }
            else false
        }
    }

    override fun setOnClickListener(l: OnClickListener?) { listener = l }

    private fun KeyboardView.reset(show: Boolean) = if (show) {
        visibility = View.VISIBLE
        input = { binding.messageInput.onCreateInputConnection(EditorInfo()) }
    } else {
        visibility = View.GONE
        input = null
    }
}
