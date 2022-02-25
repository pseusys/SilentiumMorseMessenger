package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.databinding.FragmentContactsBinding
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.mvs.ContactsViewModel
import com.ekdorn.silentium.utils.Action
import com.ekdorn.silentium.utils.DoubleItemCallback
import com.ekdorn.silentium.views.DescriptiveRecyclerView
import com.google.android.material.imageview.ShapeableImageView


class ContactsFragment : Fragment() {
    private lateinit var contactsViewModel: ContactsViewModel
    private var _binding: FragmentContactsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contactsViewModel = ViewModelProvider(requireActivity())[ContactsViewModel::class.java]
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        binding.createContact.setOnClickListener { contactsViewModel.addContact() }

        val deleteAction = Action(R.drawable.icon_delete, R.color.red, R.color.white, IntRange.EMPTY) { contactsViewModel.removeContact(it) }

        val adapter = ContactsAdapter(emptyList(), emptyList(), deleteAction)
        binding.contactsView.initRecycler(adapter, LinearLayoutManager(requireContext()))
        binding.contactsView.setItemCallback(DoubleItemCallback(requireContext(), deleteAction))

        contactsViewModel.internal.observe(viewLifecycleOwner) {
            deleteAction.views = it.indices
            adapter.sync(it, ContactsAdapter.ContactsSet.INTERNAL)
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


class ContactsAdapter(private var internal: List<Contact>, private var external: List<Contact>, private val deleteAction: Action) : DescriptiveRecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    enum class ContactsSet { INTERNAL, EXTERNAL }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactImage: ShapeableImageView = view.findViewById(R.id.contact_image)
        val contactName: TextView = view.findViewById(R.id.contact_name)
        val contactOnline: TextView = view.findViewById(R.id.contact_online)
    }

    fun sync(new: List<Contact>, set: ContactsSet) {
        val old = internal + external
        val complete = when (set) {
            ContactsSet.INTERNAL -> new + external
            ContactsSet.EXTERNAL -> internal + new
        }
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = old.size
            override fun getNewListSize() = complete.size
            override fun areItemsTheSame(oip: Int, nip: Int): Boolean {
                return old[oip] == complete[nip]
            }
            override fun areContentsTheSame(oip: Int, nip: Int): Boolean {
                val o = old[oip]
                val n = complete[nip]
                return (o.name == n.name) && (o.contact == n.contact) && (o.wasOnline == n.wasOnline)
            }
        })
        when (set) {
            ContactsSet.INTERNAL -> internal = new
            ContactsSet.EXTERNAL -> external = new
        }
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_contact, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        val contact = if (position < internal.size) internal[position] else external[position - internal.size]
        viewHolder.contactName.text = contact.name ?: contact.contact
        viewHolder.contactOnline.text = contact.wasOnline.toString()
    }

    override fun getItemCount() = internal.size + external.size

    override fun separators(): List<Pair<Int, String>> {
        return if (internal.isEmpty() && external.isEmpty()) emptyList()
        else if (internal.isEmpty()) listOf(Pair(0, "EXTERNAL"))
        else if (external.isEmpty()) listOf(Pair(0, "INTERNAL"))
        else listOf(Pair(0, "INTERNAL"), Pair(internal.size, "EXTERNAL"))
    }

    private fun onMenuItemClick(item: MenuItem, position: Int): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteAction.callback.invoke(position)
                true
            }
            else -> false
        }
    }

    override fun onClick(viewHolder: ViewHolder, position: Int) = viewHolder.itemView.findNavController().navigate(R.id.nav_messages)

    override fun onLongClick(viewHolder: ContactsAdapter.ViewHolder, position: Int) {
        if (position < internal.size) PopupMenu(viewHolder.itemView.context, viewHolder.itemView).apply {
            inflate(R.menu.fragment_contacts_menu)
            setOnMenuItemClickListener { onMenuItemClick(it, position) }
            show()
        }
    }
}
