package com.ekdorn.silentium.adapters

import android.util.Log
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
import com.ekdorn.silentium.core.toReadableString
import com.ekdorn.silentium.fragments.DialogsFragmentDirections
import com.ekdorn.silentium.models.Dialog
import com.ekdorn.silentium.views.DescriptiveRecyclerView
import com.ekdorn.silentium.visuals.VisualAction
import com.google.android.material.chip.Chip
import com.google.android.material.imageview.ShapeableImageView


class DialogsAdapter(private val deleteAction: VisualAction) : DescriptiveRecyclerView.Adapter<DialogsAdapter.ViewHolder>() {
    private var dialogs: List<Dialog> = emptyList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactImage: ShapeableImageView = view.findViewById(R.id.dialog_image)
        val contactName: TextView = view.findViewById(R.id.contact_name)
        val lastMessage: TextView = view.findViewById(R.id.last_message_text)
        val unreadCount: Chip = view.findViewById(R.id.unread_count)
    }

    fun sync(new: List<Dialog>) {
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = dialogs.size
            override fun getNewListSize() = new.size
            override fun areItemsTheSame(oip: Int, nip: Int): Boolean {
                return dialogs[oip].lastMessage == new[nip].lastMessage
            }
            override fun areContentsTheSame(oip: Int, nip: Int): Boolean {
                val o = dialogs[oip].lastMessage
                val n = new[nip].lastMessage
                val messagesSame = (o.text.contentEquals(n.text)) && (o.date == n.date) && (o.authorID == n.authorID) && (o.read == n.read)
                return messagesSame && (dialogs[oip].contact == new[nip].contact) && (dialogs[oip].unreadCount == new[nip].unreadCount)
            }
        })
        dialogs = new
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_dialog, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        viewHolder.contactName.text = dialogs[position].contact.name ?: dialogs[position].contact.contact
        viewHolder.lastMessage.text = dialogs[position].lastMessage.text.toReadableString()
        Log.e("TAG", "onBindViewHolder: ${dialogs[position].unreadCount}")
        val unread = dialogs[position].unreadCount
        if (unread > 0) {
            viewHolder.unreadCount.visibility = View.VISIBLE
            viewHolder.unreadCount.text = unread.toString()
        } else viewHolder.unreadCount.visibility = View.INVISIBLE
    }

    override fun getItemCount() = dialogs.size

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
        val action = DialogsFragmentDirections.actionNavDialogsToNavMessages(dialogs[position].contact.id)
        viewHolder.itemView.findNavController().navigate(action)
    }

    override fun onLongClick(viewHolder: ViewHolder, position: Int) {
        PopupMenu(viewHolder.itemView.context, viewHolder.itemView).apply {
            inflate(R.menu.fragment_dialogs_menu)
            setOnMenuItemClickListener { onMenuItemClick(it, position) }
            show()
        }
    }
}
