package com.ekdorn.silentium.adapters

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.fragments.ContactsFragmentDirections
import com.ekdorn.silentium.fragments.DialogsFragmentDirections
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.views.DescriptiveRecyclerView
import com.ekdorn.silentium.visuals.VisualAction
import com.google.android.material.imageview.ShapeableImageView


class ContactsAdapter(private var me: Contact, private val deleteAction: VisualAction) : DescriptiveRecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    private var internal: List<Contact> = emptyList()
    private var external: List<Contact> = emptyList()
    enum class ContactsSet { ME, INTERNAL, EXTERNAL }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactImage: ShapeableImageView = view.findViewById(R.id.contact_image)
        val contactName: TextView = view.findViewById(R.id.contact_name)
        val contactOnline: TextView = view.findViewById(R.id.contact_online)
    }

    fun sync(new: List<Contact>, set: ContactsSet) {
        val old = listOf(me) + internal + external
        val complete = when (set) {
            ContactsSet.ME -> new + internal + external
            ContactsSet.INTERNAL -> listOf(me) + new + external
            ContactsSet.EXTERNAL -> listOf(me) + internal + new
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
            ContactsSet.ME -> me = new.single()
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
        val contact = getItem(position)
        viewHolder.contactName.text = contact.name ?: contact.contact
        viewHolder.contactOnline.text = contact.wasOnline.toString()
    }

    override fun getItemCount() = 1 + internal.size + external.size

    private fun getItem(position: Int) = when {
        position == 0 -> me
        position < internal.size + 1 -> internal[position - 1]
        else -> external[position - 1 - internal.size]
    }

    override fun separators(): List<Pair<Int, String>> {
        return if (internal.isEmpty() && external.isEmpty()) emptyList()
        else if (internal.isEmpty()) listOf(Pair(1, "EXTERNAL"))
        else if (external.isEmpty()) listOf(Pair(1, "INTERNAL"))
        else listOf(Pair(1, "INTERNAL"), Pair(internal.size + 1, "EXTERNAL"))
    }

    private fun onMenuItemClick(item: MenuItem, position: Int): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteAction.callback(position)
                true
            }
            else -> false
        }
    }

    override fun onClick(viewHolder: ViewHolder, position: Int) {
        val action = ContactsFragmentDirections.actionNavContactsToNavMessages(getItem(position).id)
        viewHolder.itemView.findNavController().navigate(action)
    }

    override fun onLongClick(viewHolder: ViewHolder, position: Int) {
        if (position in deleteAction.views) PopupMenu(viewHolder.itemView.context, viewHolder.itemView).apply {
            inflate(R.menu.fragment_contacts_menu)
            setOnMenuItemClickListener { onMenuItemClick(it, position) }
            show()
        }
    }
}
