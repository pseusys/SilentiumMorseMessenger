package com.ekdorn.silentium.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.*
import com.ekdorn.silentium.databinding.FragmentDescriptionBinding


class DescriptionFragment : Fragment() {
    private var _binding: FragmentDescriptionBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDescriptionBinding.inflate(inflater, container, false)

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val dit = sharedPref.getInt(Morse.DIT_LENGTH_KEY.first, Morse.DIT_LENGTH_KEY.second)
        val dah = sharedPref.getInt(Morse.DAH_LENGTH_KEY.first, Morse.DAH_LENGTH_KEY.second)
        val gap = sharedPref.getInt(Morse.GAP_LENGTH_KEY.first, Morse.GAP_LENGTH_KEY.second)
        val end = sharedPref.getInt(Morse.END_LENGTH_KEY.first, Morse.END_LENGTH_KEY.second)
        val eom = sharedPref.getInt(Morse.EOM_LENGTH_KEY.first, Morse.EOM_LENGTH_KEY.second)

        val morseCode = Morse.codeData().sortedBy { it.value }.map { "${it.value}: ${it.key.toBiBits().biBitsToMyte().toMorseString()}" }

        binding.codeDescription.text = getString(R.string.description_code_description, Morse.name, Morse.ref)
        binding.timeDescription.text = getString(R.string.description_time_description, dit, dah, gap, end, eom)
        binding.codeViewCol1.text = morseCode.subList(0, morseCode.size / 2).joinToString("\n")
        binding.codeViewCol2.text = morseCode.subList(morseCode.size / 2, morseCode.size).joinToString("\n")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
