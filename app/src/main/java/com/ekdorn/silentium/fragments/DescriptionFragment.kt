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

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDescriptionBinding.inflate(inflater, container, false)

        val prefs = PreferenceManager[requireContext()]
        val morseCode = Morse.codeData().sortedBy { it.value }.map { "${it.value}: ${it.key.toMorseString()}" }

        binding.codeDescription.text = getString(R.string.description_code_description, Morse.name, Morse.ref)
        binding.codeViewCol1.text = morseCode.subList(0, morseCode.size / 2).joinToString("\n")
        binding.codeViewCol2.text = morseCode.subList(morseCode.size / 2, morseCode.size).joinToString("\n")
        binding.timeView.text = getString(R.string.description_time_view, prefs.get<Int>(DAH_LENGTH_KEY), prefs.get<Int>(GAP_LENGTH_KEY), prefs.get<Int>(END_LENGTH_KEY), prefs.get<Int>(EOM_LENGTH_KEY))

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
