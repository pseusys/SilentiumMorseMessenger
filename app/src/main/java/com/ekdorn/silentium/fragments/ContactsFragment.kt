package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ekdorn.silentium.databinding.FragmentContactsBinding
import com.ekdorn.silentium.databinding.FragmentDialogsBinding
import com.ekdorn.silentium.models.ContactsViewModel
import com.google.android.material.snackbar.Snackbar

class ContactsFragment : Fragment() {

    private lateinit var contactsViewModel: ContactsViewModel
    private var _binding: FragmentContactsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contactsViewModel = ViewModelProvider(this)[ContactsViewModel::class.java]

        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.importContacts.setOnClickListener { view ->
            Snackbar.make(view, "Import contact", Snackbar.LENGTH_LONG).setAction("Action", null).show()
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
