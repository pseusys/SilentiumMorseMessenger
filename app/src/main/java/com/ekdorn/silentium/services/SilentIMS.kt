package com.ekdorn.silentium.services

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.ekdorn.silentium.activities.IMSSettingsActivity
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.databinding.ViewKeyboardBinding
import com.ekdorn.silentium.views.SilentInputView


class SilentIMS: InputMethodService() {
    override fun onCreateInputView(): View {
        val binding = ViewKeyboardBinding.inflate(layoutInflater)

        binding.inputButton.addMorseListener(object : SilentInputView.MorseListener() {
            val bibits = mutableListOf<BiBit>()

            override fun onBiBit(biBit: BiBit) {
                bibits.add(biBit)
                currentInputConnection.setComposingText(bibits.map { it.sign }.joinToString(""), 1)
            }

            override fun onLong(long: Long) {
                bibits.clear()
                currentInputConnection.commitText(Morse.getString(long), 1)

            }

            override fun onMyte(myte: Myte) {
                currentInputConnection.deleteSurroundingText(1, 0)
                currentInputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
        })

        binding.backButton.setOnClickListener { currentInputConnection.deleteSurroundingText(1, 0) }
        binding.settingsButton.setOnClickListener { startActivity(Intent(this, IMSSettingsActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }

        return binding.root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        // TODO: check input types (https://developer.android.com/guide/topics/text/creating-input-method#handle-different-input-types)
    }
}
