package com.ekdorn.silentium.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekdorn.silentium.R
import com.ekdorn.silentium.mvs.ContactsViewModel
import com.ekdorn.silentium.visuals.VisualAction
import com.ekdorn.silentium.visuals.DoubleItemCallback
import com.ekdorn.silentium.adapters.ContactsAdapter
import com.ekdorn.silentium.databinding.DialogContactBinding
import com.ekdorn.silentium.databinding.FragmentContactsBinding
import com.ekdorn.silentium.managers.NetworkManager
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.models.Contact
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch


class ContactsFragment : Fragment() {
    private val contactsViewModel by activityViewModels<ContactsViewModel>()

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val userData = UserManager[requireContext()]
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        binding.createContact.setOnClickListener {
            val dialog = ContactDialogFragment()
            dialog.listener = object : ContactDialogFragment.ContactDialogListener {
                override fun onContactLoaded(contact: Contact) = contactsViewModel.addContact(contact).ensureActive()
            }
            dialog.show(childFragmentManager, "DIALOG")
        }

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


class ContactDialogFragment : DialogFragment() {
    lateinit var listener: ContactDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var current: String? = null

        val dialog = DialogContactBinding.inflate(requireActivity().layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialog.root)
            .setTitle("Find a contact").setMessage("Start typing user's email or phone number")
            .setPositiveButton("Create contact") { _, _ ->
                lifecycleScope.launch {
                    current?.let { listener.onContactLoaded(NetworkManager.updateContact(it)) }
                }
            }
            .setNegativeButton("Cancel") {
                d, _ -> d.cancel()
            }
        val built =  builder.create()

        dialog.contactInput.doOnTextChanged { text, _, _, _ ->
            lifecycleScope.launch {
                val data = if (text != null) NetworkManager.findContacts(text.toString()) ?: mapOf() else mapOf()
                if (data.size == 1) {
                    built.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    current = data.entries.single().key
                } else {
                    built.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    current = null
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, data.values.toList())
                dialog.contactInput.setAdapter(adapter)
            }
        }
        return built
    }

    interface ContactDialogListener {
        fun onContactLoaded(contact: Contact)
    }
}
