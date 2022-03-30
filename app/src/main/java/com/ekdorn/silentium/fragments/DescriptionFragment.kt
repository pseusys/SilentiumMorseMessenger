package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.*
import com.ekdorn.silentium.databinding.FragmentDescriptionBinding
import com.ekdorn.silentium.managers.PreferenceManager
import com.ekdorn.silentium.managers.PreferenceManager.DAH_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.END_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.EOM_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.GAP_LENGTH_KEY
import com.ekdorn.silentium.managers.PreferenceManager.get


class DescriptionFragment : Fragment() {
    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDescriptionBinding.inflate(inflater, container, false)

        val prefs = PreferenceManager[requireContext()]
        val morseCode = Morse.codeData().sortedBy { it.value }.map { "${it.value}: ${it.key.toMorseString()}" }

        val dahLen = prefs.get<Long>(DAH_LENGTH_KEY)
        val gapLen = prefs.get<Long>(GAP_LENGTH_KEY)
        val endLen = prefs.get<Long>(END_LENGTH_KEY)
        val eomLen = prefs.get<Long>(EOM_LENGTH_KEY)

        binding.codeDescription.text = getString(R.string.description_code_description, Morse.name, Morse.ref)
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
