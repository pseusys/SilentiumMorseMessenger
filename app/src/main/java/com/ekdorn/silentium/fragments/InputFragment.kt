package com.ekdorn.silentium.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.databinding.FragmentInputRootBinding
import com.ekdorn.silentium.views.SilentInput


class InputFragment : Fragment() {
    private var _binding: FragmentInputRootBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInputRootBinding.inflate(inflater, container, false)

        val inputCard = binding.inputView.inputCard

        binding.inputButton.addMorseListener(object : SilentInput.MorseListener() {
            override fun onStart() {
                binding.inputView.morseInput.text = ""
                binding.inputView.precessedInput.text = ""
                inputCard.x = -inputCard.width.toFloat()
                inputCard.animate().alpha(1F).translationXBy(inputCard.width.toFloat()).setStartDelay(0L).start()
            }

            @SuppressLint("SetTextI18n")
            override fun onBiBit(biBit: BiBit?) {
                if (biBit == null) return
                val text = binding.inputView.morseInput.text
                binding.inputView.morseInput.text = "$text$biBit "
            }

            @SuppressLint("SetTextI18n")
            override fun onLong(long: Long?) {
                if (long == null) return
                binding.inputView.morseInput.text = ""
                val text = binding.inputView.precessedInput.text
                binding.inputView.precessedInput.text = "$text${Morse.getString(long)}"
            }

            override fun onMyte(myte: Myte) {
                inputCard.animate().alpha(0F).translationXBy(inputCard.width.toFloat()).setStartDelay(1000L).start()
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
