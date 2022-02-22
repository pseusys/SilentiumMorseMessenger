package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ekdorn.silentium.core.*
import com.ekdorn.silentium.databinding.FragmentDialogsBinding
import com.ekdorn.silentium.mvs.DialogsViewModel
import com.google.android.material.snackbar.Snackbar


class DialogsFragment : Fragment() {

    private lateinit var dialogsViewModel: DialogsViewModel
    private var _binding: FragmentDialogsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialogsViewModel = ViewModelProvider(this)[DialogsViewModel::class.java]

        _binding = FragmentDialogsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.createDialog.setOnClickListener { view ->
            val code = listOf(BiBit.DIT, BiBit.DIT, BiBit.DIT, BiBit.GAP, BiBit.DAH, BiBit.DAH, BiBit.DAH, BiBit.GAP, BiBit.DIT, BiBit.DIT, BiBit.DIT)
            Snackbar.make(view, code.biBitsToMyte().toReadableString(), Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }

        //val textView: TextView = binding.textGallery
        //dialogsViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
