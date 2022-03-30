package com.ekdorn.silentium.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse
import com.ekdorn.silentium.core.Myte


class MorsePreView(context: Context, attributes: AttributeSet?, style: Int) : ConstraintLayout(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    private val inputCard: CardView
    private val processedInput: TextView
    private val morseInput: TextView

    init {
        View.inflate(context, R.layout.view_morse_preview, this)
        inputCard = findViewById(R.id.input_card)
        processedInput = findViewById(R.id.contacts_header)
        morseInput = findViewById(R.id.morse_input)
    }

    val listener = object : SilentInputView.MorseListener() {
        override fun onStart() {
            morseInput.text = ""
            processedInput.text = ""
            inputCard.x = -inputCard.width.toFloat()
            inputCard.animate().alpha(1F).translationXBy(inputCard.width.toFloat()).setStartDelay(0L).start()
        }

        @SuppressLint("SetTextI18n")
        override fun onBiBit(biBit: BiBit) {
            morseInput.text = "${morseInput.text}$biBit "
        }

        @SuppressLint("SetTextI18n")
        override fun onLong(long: Long) {
            morseInput.text = ""
            processedInput.text = "${processedInput.text}${Morse.getString(long)}"
        }

        override fun onMyte(myte: Myte) {
            inputCard.animate().alpha(0F).translationXBy(inputCard.width.toFloat()).setStartDelay(1000L).start()
        }
    }
}
