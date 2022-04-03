package com.ekdorn.silentium.views

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.InputConnection
import androidx.constraintlayout.widget.ConstraintLayout
import com.ekdorn.silentium.activities.IMSSettingsActivity
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.databinding.ViewKeyboardBinding


class KeyboardView(context: Context, attributes: AttributeSet?, style: Int) : ConstraintLayout(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    private val binding = ViewKeyboardBinding.inflate(LayoutInflater.from(context), this, true)
    var input: (() -> InputConnection)? = null

    init {
        binding.inputButton.addMorseListener(object : SilentInputView.MorseListener() {
            val bibits = mutableListOf<BiBit>()

            override fun onBiBit(biBit: BiBit) {
                bibits.add(biBit)
                input?.invoke()?.setComposingText(bibits.map { it.sign }.joinToString(""), 1)
            }

            override fun onLong(long: Long) {
                bibits.clear()
                input?.invoke()?.commitText(Morse.getString(long), 1)
            }

            override fun onMyte(myte: Myte) {
                input?.invoke()?.deleteSurroundingText(1, 0)
                input?.invoke()?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
        })

        binding.backButton.setOnClickListener { input?.invoke()?.deleteSurroundingText(1, 0) }
        binding.settingsButton.setOnClickListener { context.startActivity(Intent(context, IMSSettingsActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }
}
