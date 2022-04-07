package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.*
import com.ekdorn.silentium.core.Morse.morse
import com.ekdorn.silentium.databinding.FragmentDescriptionBinding
import com.ekdorn.silentium.managers.PreferenceManager


class DescriptionFragment : Fragment() {
    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDescriptionBinding.inflate(inflater, container, false)

        val prefs = PreferenceManager[requireContext()]
        val morse = requireContext().morse()
        val morseCode = morse.codeData().sortedBy { it.value }.map { "${it.value}: ${it.key.toMorseString()}" }

        val dahLen = prefs.get(R.string.pref_morse_dah_key, -1)
        val gapLen = prefs.get(R.string.pref_morse_gap_key, -1)
        val endLen = prefs.get(R.string.pref_morse_end_key, -1)
        val eomLen = prefs.get(R.string.pref_morse_eom_key, -1)

        binding.codeDescription.text = getString(R.string.description_code_description, morse.name, morse.ref)
        binding.codeViewCol1.text = morseCode.subList(0, morseCode.size / 2).joinToString("\n")
        binding.codeViewCol2.text = morseCode.subList(morseCode.size / 2, morseCode.size).joinToString("\n")
        binding.timeView.text = getString(R.string.description_time_view, dahLen, gapLen, endLen, eomLen)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Long.toMorseString() = toBiBits().fold("") { acc, bb -> "${acc}${bb.sign}" }
}
