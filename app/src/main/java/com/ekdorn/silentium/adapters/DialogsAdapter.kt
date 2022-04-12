package com.ekdorn.silentium.adapters

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.toReadableString
import com.ekdorn.silentium.databinding.ItemDialogBinding
import com.ekdorn.silentium.fragments.DialogsFragmentDirections
import com.ekdorn.silentium.managers.NetworkManager
import com.ekdorn.silentium.models.Dialog
import com.ekdorn.silentium.views.DescriptiveRecyclerView
import com.ekdorn.silentium.visuals.VisualAction


class DialogsAdapter(private val deleteAction: VisualAction) : DescriptiveRecyclerView.Adapter<DialogsAdapter.ViewHolder>() {
    private var dialogs: List<Dialog> = emptyList()

    inner class ViewHolder(val binding: ItemDialogBinding) : RecyclerView.ViewHolder(binding.root)

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
                val messagesSame = (o.payload.data == n.payload.data) && (o.date == n.date) && (o.authorID == n.authorID) && (o.read == n.read)
                return messagesSame && (dialogs[oip].contact == new[nip].contact) && (dialogs[oip].unreadCount == new[nip].unreadCount)
            }
        })
        dialogs = new
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = ViewHolder(ItemDialogBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        if (dialogs[position].contact.avatar != null) Glide.with(viewHolder.itemView.context).setDefaultRequestOptions(NetworkManager.options).load(dialogs[position].contact.avatar).into(viewHolder.binding.dialogImage)
        viewHolder.binding.contactName.text = dialogs[position].contact.name ?: dialogs[position].contact.contact
        viewHolder.binding.lastMessage.text = dialogs[position].lastMessage.payload.data.toReadableString(viewHolder.itemView.context)
        val unread = dialogs[position].unreadCount
        if (unread > 0) {
            viewHolder.binding.unreadCount.visibility = View.VISIBLE
            viewHolder.binding.unreadCount.text = unread.toString()
        } else viewHolder.binding.unreadCount.visibility = View.INVISIBLE
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
