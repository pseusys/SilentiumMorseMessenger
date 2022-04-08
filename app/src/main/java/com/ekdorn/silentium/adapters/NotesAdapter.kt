package com.ekdorn.silentium.adapters

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.toReadableString
import com.ekdorn.silentium.databinding.ItemNoteBinding
import com.ekdorn.silentium.managers.ClipboardManager
import com.ekdorn.silentium.models.Note
import com.ekdorn.silentium.views.DescriptiveRecyclerView
import com.ekdorn.silentium.visuals.VisualAction


class NotesAdapter(private val deleteAction: VisualAction, private val sendAction: VisualAction) : DescriptiveRecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    private var notes: List<Note> = emptyList()

    inner class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    fun sync(new: List<Note>) {
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = notes.size
            override fun getNewListSize() = new.size
            override fun areItemsTheSame(oip: Int, nip: Int): Boolean {
                return notes[oip] == new[nip]
            }
            override fun areContentsTheSame(oip: Int, nip: Int): Boolean {
                val o = notes[oip]
                val n = new[nip]
                return (o.text == n.text) && (o.date == n.date)
            }
        })
        notes = new
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = ViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        viewHolder.binding.dateTimeView.text = notes[position].date.toString()
        viewHolder.binding.textView.text = notes[position].text.toReadableString(viewHolder.itemView.context)
    }

    override fun getItemCount() = notes.size

    private fun onMenuItemClick(item: MenuItem, viewHolder: ViewHolder, position: Int): Boolean {
        return when (item.itemId) {
            R.id.action_copy -> {
                ClipboardManager[viewHolder.itemView.context].set(viewHolder.binding.textView.text.toString())
                true
            }
            R.id.action_edit -> {
                // TODO: action edit
                true
            }
            R.id.action_send -> {
                sendAction.callback(position)
                true
            }
            R.id.action_delete -> {
                deleteAction.callback(position)
                true
            }
            else -> false
        }
    }

    override fun onClick(viewHolder: ViewHolder, position: Int) {}

    override fun onLongClick(viewHolder: ViewHolder, position: Int) {
        PopupMenu(viewHolder.itemView.context, viewHolder.itemView).apply {
            inflate(R.menu.fragment_notes_menu)
            setOnMenuItemClickListener { onMenuItemClick(it, viewHolder, position) }
            show()
        }
    }
}
