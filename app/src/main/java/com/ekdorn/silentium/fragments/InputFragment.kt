package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Myte
import com.ekdorn.silentium.databinding.FragmentInputBinding
import com.ekdorn.silentium.mvs.NotesViewModel
import com.ekdorn.silentium.views.SilentInputView


class InputFragment : Fragment() {
    private lateinit var notesViewModel: NotesViewModel
    private var _binding: FragmentInputBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        notesViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        _binding = FragmentInputBinding.inflate(inflater, container, false)

        _binding!!.inputButton.addMorseListener(object : SilentInputView.MorseListener() {
            override fun onStart() = _binding?.inputView?.listener?.onStart() ?: Unit
            override fun onBiBit(biBit: BiBit?) = _binding?.inputView?.listener?.onBiBit(biBit) ?: Unit
            override fun onLong(long: Long?) = _binding?.inputView?.listener?.onLong(long) ?: Unit
            override fun onMyte(myte: Myte) {
                _binding?.inputView?.listener?.onMyte(myte)
                notesViewModel.saveNote(myte)
            }
        })

        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
