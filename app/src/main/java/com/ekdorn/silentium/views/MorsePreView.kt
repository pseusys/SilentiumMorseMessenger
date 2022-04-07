package com.ekdorn.silentium.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse.morse
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.databinding.ViewMorsePreviewBinding


class MorsePreView(context: Context, attributes: AttributeSet?, style: Int) : ConstraintLayout(context, attributes, style) {
    constructor(context: Context, attributes: AttributeSet?): this(context, attributes, 0)
    constructor(context: Context): this(context, null)

    private val binding = ViewMorsePreviewBinding.inflate(LayoutInflater.from(context), this, true)

    val listener = object : SilentInputView.MorseListener() {
        override fun onStart() {
            binding.morseInput.text = ""
            binding.processedInput.text = ""
            binding.inputCard.x = -binding.inputCard.width.toFloat()
            binding.inputCard.animate().alpha(1F).translationXBy(binding.inputCard.width.toFloat()).setStartDelay(0L).start()
        }

        @SuppressLint("SetTextI18n")
        override fun onBiBit(biBit: BiBit) {
            binding.morseInput.text = "${binding.morseInput.text}$biBit "
        }

        @SuppressLint("SetTextI18n")
        override fun onLong(long: Long) {
            binding.morseInput.text = ""
            binding.processedInput.text = "${binding.processedInput.text}${context.morse().getString(long)}"
        }

        override fun onMyte(myte: Myte) {
            binding.inputCard.animate().alpha(0F).translationXBy(binding.inputCard.width.toFloat()).setStartDelay(1000L).start()
        }
    }
}
