package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekdorn.silentium.R
import com.ekdorn.silentium.databinding.FragmentNotesBinding
import com.ekdorn.silentium.mvs.NotesViewModel
import com.ekdorn.silentium.visuals.VisualAction
import com.ekdorn.silentium.visuals.DoubleItemCallback
import com.ekdorn.silentium.adapters.NotesAdapter


class NotesFragment : Fragment() {
    private val notesViewModel by viewModels<NotesViewModel>({ requireActivity() })

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)

        var adapter: NotesAdapter? = null

        val deleteAction = VisualAction(R.drawable.icon_delete, R.color.red, R.color.white, IntRange.EMPTY) { notesViewModel.removeNote(it) }
        val sendAction = VisualAction(R.drawable.icon_send, R.color.blue, R.color.white, IntRange.EMPTY) {
            notesViewModel.sendNote(it)
            adapter?.notifyItemChanged(it)
        }

        adapter = NotesAdapter(deleteAction, sendAction)
        binding.notesView.initRecycler(adapter, LinearLayoutManager(requireContext()))
        binding.notesView.setItemCallback(DoubleItemCallback(requireContext(), deleteAction, sendAction))

        notesViewModel.notes.observe(viewLifecycleOwner) {
            deleteAction.views = it.indices
            sendAction.views = it.indices
            adapter.sync(it)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
