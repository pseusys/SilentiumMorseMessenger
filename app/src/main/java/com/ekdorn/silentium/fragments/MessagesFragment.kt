package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ekdorn.silentium.databinding.FragmentMessagesBinding
import com.ekdorn.silentium.mvs.NotesViewModel


class MessagesFragment : Fragment() {
    private lateinit var homeViewModel: NotesViewModel
    private var _binding: FragmentMessagesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // homeViewModel = ViewModelProvider(this)[NotesViewModel::class.java]

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // val textView: TextView = binding.textHome
        // homeViewModel.text.observe(viewLifecycleOwner) { textView.text = it }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
