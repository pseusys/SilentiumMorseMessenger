package com.ekdorn.silentium.services

import android.inputmethodservice.InputMethodService
import android.view.inputmethod.EditorInfo
import com.ekdorn.silentium.databinding.KeyboardMainBinding


class SilentIMS: InputMethodService() {
    override fun onCreateInputView() = KeyboardMainBinding.inflate(layoutInflater).apply { root.input = { currentInputConnection } }.root

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        // TODO: check input types (https://developer.android.com/guide/topics/text/creating-input-method#handle-different-input-types)
    }
}
