package com.ekdorn.silentium.services

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.EditorInfo
import com.ekdorn.silentium.core.Morse.morse
import com.ekdorn.silentium.views.KeyboardView


class SilentIMS: InputMethodService() {
    private lateinit var keyboard: KeyboardView

    override fun onCreateInputView(): View {
        keyboard = KeyboardView(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            input = { currentInputConnection }
        }
        return keyboard
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        keyboard.morse = this.morse()
        // TODO: check input types (https://developer.android.com/guide/topics/text/creating-input-method#handle-different-input-types)
    }
}
