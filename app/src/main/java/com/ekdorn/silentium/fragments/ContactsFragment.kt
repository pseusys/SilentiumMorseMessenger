package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekdorn.silentium.R
import com.ekdorn.silentium.mvs.ContactsViewModel
import com.ekdorn.silentium.visuals.VisualAction
import com.ekdorn.silentium.visuals.DoubleItemCallback
import com.ekdorn.silentium.adapters.ContactsAdapter
import com.ekdorn.silentium.databinding.FragmentContactsBinding
import com.ekdorn.silentium.managers.UserManager


class ContactsFragment : Fragment() {
    private lateinit var contactsViewModel: ContactsViewModel
    private var _binding: FragmentContactsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val userData = UserManager[requireContext()]
        contactsViewModel = ViewModelProvider(requireActivity())[ContactsViewModel::class.java]
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        binding.createContact.setOnClickListener { contactsViewModel.addContact() }

        val deleteAction = VisualAction(R.drawable.icon_delete, R.color.red, R.color.white, IntRange.EMPTY) { contactsViewModel.removeContact(it - 1) }

        val adapter = ContactsAdapter(userData.value!!, deleteAction)
        binding.contactsView.initRecycler(adapter, LinearLayoutManager(requireContext()))
        binding.contactsView.setItemCallback(DoubleItemCallback(requireContext(), deleteAction))

        userData.observe(viewLifecycleOwner) {
            adapter.sync(listOf(it), ContactsAdapter.ContactsSet.ME)
        }
        contactsViewModel.internal.observe(viewLifecycleOwner) {
            val meRemoved = it.filter { contact -> contact.id != userData.value!!.id }
            deleteAction.views = IntRange(meRemoved.indices.first + 1, meRemoved.indices.last + 1)
            adapter.sync(meRemoved, ContactsAdapter.ContactsSet.INTERNAL)
        }
        contactsViewModel.external.observe(viewLifecycleOwner) {
            adapter.sync(it, ContactsAdapter.ContactsSet.EXTERNAL)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
