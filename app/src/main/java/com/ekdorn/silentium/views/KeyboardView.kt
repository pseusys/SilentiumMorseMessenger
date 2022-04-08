package com.ekdorn.silentium.views

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.InputConnection
import androidx.constraintlayout.widget.ConstraintLayout
import com.ekdorn.silentium.R
import com.ekdorn.silentium.activities.ProxyActivity
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse.morse
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.databinding.ViewKeyboardBinding
import com.ekdorn.silentium.fragments.SettingsFragment
import com.ekdorn.silentium.managers.PreferenceManager


class KeyboardView(context: Context, attributes: AttributeSet?, style: Int) : ConstraintLayout(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    private val binding = ViewKeyboardBinding.inflate(LayoutInflater.from(context), this, true)
    private val append = PreferenceManager[context].get(R.string.pref_morse_append_key, false)

    val locales: Array<String> = context.resources.getStringArray(R.array.pref_morse_language_entry_values)

    var input: (() -> InputConnection?)? = null
    var morse = context.morse()
        set(value) {
            field = value
            binding.languageDisplay.text = field.flag
        }

    init {
        binding.inputButton.addMorseListener(object : SilentInputView.MorseListener() {
            val bibits = mutableListOf<BiBit>()

            override fun onStart() = freeze(true)

            override fun onBiBit(biBit: BiBit) {
                bibits.add(biBit)
                input?.invoke()?.setComposingText(bibits.map { it.sign }.joinToString(""), 1)
            }

            override fun onLong(long: Long) {
                bibits.clear()
                input?.invoke()?.commitText(morse.getString(long), 1)
            }

            override fun onMyte(myte: Myte) {
                input?.invoke()?.deleteSurroundingText(1, 0)
                if (append) input?.invoke()?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                freeze(false)
            }
        })

        binding.backButton.setOnClickListener { input?.invoke()?.deleteSurroundingText(1, 0) }
        binding.settingsButton.setOnClickListener {
            val intent = Intent(context, ProxyActivity::class.java).putExtra(SettingsFragment.keyboard, true)
            context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK))
        }

        binding.languageDisplay.text = morse.flag
        binding.languageButton.setOnClickListener {
            val ind = locales.indexOf(morse.language)
            morse = morse(if (ind == locales.size - 1) locales[0] else locales[ind + 1])
        }
    }

    fun freeze(switch: Boolean) {
        if (switch) input?.invoke()?.beginBatchEdit()
        else input?.invoke()?.endBatchEdit()
        binding.backButton.isEnabled = !switch
        binding.languageButton.isEnabled = !switch
    }
}
