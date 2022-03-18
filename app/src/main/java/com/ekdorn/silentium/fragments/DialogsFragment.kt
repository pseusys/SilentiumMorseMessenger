package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekdorn.silentium.R
import com.ekdorn.silentium.databinding.FragmentDialogsBinding
import com.ekdorn.silentium.mvs.DialogsViewModel
import com.ekdorn.silentium.visuals.VisualAction
import com.ekdorn.silentium.adapters.DialogsAdapter
import com.ekdorn.silentium.visuals.DoubleItemCallback


class DialogsFragment : Fragment() {
    private lateinit var dialogsViewModel: DialogsViewModel
    private var _binding: FragmentDialogsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialogsViewModel = ViewModelProvider(requireActivity())[DialogsViewModel::class.java]
        _binding = FragmentDialogsBinding.inflate(inflater, container, false)

        val deleteAction = VisualAction(R.drawable.icon_delete, R.color.red, R.color.white, IntRange.EMPTY) { dialogsViewModel.removeDialog(it) }

        val adapter = DialogsAdapter(deleteAction)
        binding.dialogsView.initRecycler(adapter, LinearLayoutManager(requireContext()))
        binding.dialogsView.setItemCallback(DoubleItemCallback(requireContext(), deleteAction))

        dialogsViewModel.dialogs.observe(viewLifecycleOwner) {
            deleteAction.views = it.indices
            adapter.sync(it)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
