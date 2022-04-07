package com.ekdorn.silentium.services

import android.inputmethodservice.InputMethodService
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.EditorInfo
import com.ekdorn.silentium.views.KeyboardView


class SilentIMS: InputMethodService() {
    override fun onCreateInputView() = KeyboardView(this).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        input = { currentInputConnection }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        // TODO: check input types (https://developer.android.com/guide/topics/text/creating-input-method#handle-different-input-types)
    }
}
