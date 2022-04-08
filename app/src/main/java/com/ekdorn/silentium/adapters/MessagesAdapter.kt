package com.ekdorn.silentium.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.toReadableString
import com.ekdorn.silentium.databinding.ItemMessageBinding
import com.ekdorn.silentium.managers.ClipboardManager
import com.ekdorn.silentium.models.Contact
import com.ekdorn.silentium.models.Message
import com.ekdorn.silentium.views.DescriptiveRecyclerView
import com.ekdorn.silentium.visuals.VisualAction


class MessagesAdapter(context: Context, var me: Contact, private val deleteAction: VisualAction) : DescriptiveRecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    private var messages: List<Message> = emptyList()

    private val incomingMessage = ContextCompat.getDrawable(context, R.drawable.shape_message_in)
    private val outcomingMessage = ContextCompat.getDrawable(context, R.drawable.shape_message_out)

    private val incomingText = ContextCompat.getColor(context, R.color.white)
    private val outcomingText = ContextCompat.getColor(context, R.color.black)

    inner class ViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun configureMessage(incoming: Boolean = false) {
            val constraintSet = ConstraintSet()
            val color: Int
            constraintSet.clone(binding.root)
            if (incoming) {
                color = incomingText
                binding.messageShape.background = incomingMessage!!.constantState!!.newDrawable().mutate()
                constraintSet.clear(R.id.message_shape, ConstraintSet.RIGHT)
                constraintSet.connect(R.id.message_shape, ConstraintSet.LEFT, R.id.root, ConstraintSet.LEFT, 0)
            } else {
                color = outcomingText
                binding.messageShape.background = outcomingMessage!!.constantState!!.newDrawable().mutate()
                constraintSet.clear(R.id.message_shape, ConstraintSet.LEFT)
                constraintSet.connect(R.id.message_shape, ConstraintSet.RIGHT, R.id.root, ConstraintSet.RIGHT, 0)
            }
            constraintSet.applyTo(binding.root)
            itemView.invalidate()
            binding.textView.setTextColor(color)
            binding.dateTimeView.setTextColor(color)
        }
    }

    fun sync(new: List<Message>) {
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = messages.size
            override fun getNewListSize() = new.size
            override fun areItemsTheSame(oip: Int, nip: Int): Boolean {
                return messages[oip] == new[nip]
            }
            override fun areContentsTheSame(oip: Int, nip: Int): Boolean {
                val o = messages[oip]
                val n = new[nip]
                return (o.payload.data == n.payload.data) && (o.date == n.date) && (o.authorID == n.authorID) && (o.read == n.read)
            }
        })
        messages = new
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = ViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        viewHolder.configureMessage(messages[position].authorID != me.id)
        viewHolder.binding.dateTimeView.text = messages[position].date.toString()
        viewHolder.binding.textView.text = messages[position].payload.data.toReadableString(viewHolder.itemView.context)
        viewHolder.binding.textView.forceLayout()
    }

    override fun getItemCount() = messages.size

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
            R.id.action_delete -> {
                deleteAction.callback(position)
                true
            }
            else -> false
        }
    }

    override fun onClick(viewHolder: ViewHolder, position: Int) {}

    override fun onLongClick(viewHolder: ViewHolder, position: Int) {
        PopupMenu(viewHolder.itemView.context, viewHolder.binding.messageShape).apply {
            inflate(R.menu.fragment_messages_menu)
            setOnMenuItemClickListener { onMenuItemClick(it, viewHolder, position) }
            show()
        }
    }
}
