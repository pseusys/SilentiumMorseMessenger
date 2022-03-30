package com.ekdorn.silentium.services

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.databinding.ViewKeyboardBinding
import com.ekdorn.silentium.views.SilentInputView


class SilentIMS: InputMethodService() {
    private var _binding: ViewKeyboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateInputView(): View {
        _binding = ViewKeyboardBinding.inflate(layoutInflater)
        val ic = currentInputConnection

        binding.inputButton.addMorseListener(object : SilentInputView.MorseListener() {
            val bibits = mutableListOf<BiBit>()

            override fun onBiBit(biBit: BiBit) {
                bibits.add(biBit)
                Toast.makeText(applicationContext, "bibit", Toast.LENGTH_SHORT).show()
                ic.setComposingText(bibits.map { it.sign }.joinToString(""), 1)
            }

            override fun onLong(long: Long) {
                bibits.clear()
                Toast.makeText(applicationContext, "long", Toast.LENGTH_SHORT).show()
                ic.commitText(Morse.getString(long), 1)
            }

            override fun onMyte(myte: Myte) {
                ic.deleteSurroundingText(1, 0)
                Toast.makeText(applicationContext, "myte", Toast.LENGTH_SHORT).show()
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
        })

        binding.backButton.setOnClickListener { ic.deleteSurroundingText(1, 0) }

        return binding.root
    }
}
