package com.ekdorn.silentium.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextClock
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.core.toReadableString
import com.ekdorn.silentium.databinding.FragmentMessagesBinding
import com.ekdorn.silentium.managers.ClipboardManager
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.models.Message
import com.ekdorn.silentium.mvs.MessagesViewModel
import com.ekdorn.silentium.utils.Action
import com.ekdorn.silentium.views.DescriptiveRecyclerView


class MessagesFragment : Fragment() {
    private lateinit var messagesViewModel: MessagesViewModel
    private var _binding: FragmentMessagesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        messagesViewModel = ViewModelProvider(requireActivity())[MessagesViewModel::class.java]
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        binding.sendButton.setOnClickListener {
            val text = binding.messageInput.text.toString()
            if (text.isNotBlank()) {
                messagesViewModel.addMessage(text.toMyteReadable())
                binding.messageInput.text.clear()
            }
        }

        val deleteAction = Action(R.drawable.icon_delete, R.color.red, R.color.white, IntRange.EMPTY) { messagesViewModel.removeMessage(it) }
        val adapter = MessagesAdapter(requireContext(), emptyList(), deleteAction)
        val recycler = binding.messagesView.initRecycler(adapter, LinearLayoutManager(requireContext()))

        messagesViewModel.messages.observe(viewLifecycleOwner) {
            deleteAction.views = it.indices
            adapter.sync(it)
            recycler.smoothScrollToPosition(it.size - 1)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class MessagesAdapter(context: Context, private var messages: List<Message>, private val deleteAction: Action) : DescriptiveRecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    private val incomingMessage = ContextCompat.getDrawable(context, R.drawable.shape_message_in)
    private val outcomingMessage = ContextCompat.getDrawable(context, R.drawable.shape_message_out)

    private val incomingText = ContextCompat.getColor(context, R.color.white)
    private val outcomingText = ContextCompat.getColor(context, R.color.black)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val root: ConstraintLayout = view.findViewById(R.id.root)
        val shape: CardView = view.findViewById(R.id.message_shape)
        val textView: TextView = view.findViewById(R.id.text_view)
        val dateTime: TextClock = view.findViewById(R.id.date_time_view)

        fun configureMessage(incoming: Boolean = false) {
            val constraintSet = ConstraintSet()
            val color: Int
            constraintSet.clone(root)
            if (incoming) {
                color = incomingText
                shape.background = incomingMessage!!.constantState!!.newDrawable().mutate()
                constraintSet.clear(R.id.message_shape, ConstraintSet.RIGHT)
                constraintSet.connect(R.id.message_shape, ConstraintSet.LEFT, R.id.root, ConstraintSet.LEFT, 0)
            } else {
                color = outcomingText
                shape.background = outcomingMessage!!.constantState!!.newDrawable().mutate()
                constraintSet.clear(R.id.message_shape, ConstraintSet.LEFT)
                constraintSet.connect(R.id.message_shape, ConstraintSet.RIGHT, R.id.root, ConstraintSet.RIGHT, 0)
            }
            constraintSet.applyTo(root)
            itemView.invalidate()
            textView.setTextColor(color)
            dateTime.setTextColor(color)
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
                return (o.text.contentEquals(n.text)) && (o.date == n.date) && (o.author == n.author) && (o.read == n.read)
            }
        })
        messages = new
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_message, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        viewHolder.configureMessage(messages[position].author != UserManager.me)
        viewHolder.dateTime.text = messages[position].date.toString()
        viewHolder.textView.text = messages[position].text.toReadableString()
        viewHolder.textView.forceLayout()
    }

    override fun getItemCount() = messages.size

    private fun onMenuItemClick(item: MenuItem, viewHolder: MessagesAdapter.ViewHolder, position: Int): Boolean {
        return when (item.itemId) {
            R.id.action_copy -> {
                ClipboardManager[viewHolder.itemView.context].set(viewHolder.textView.text.toString())
                true
            }
            R.id.action_edit -> {
                // TODO: action edit
                true
            }
            R.id.action_delete -> {
                deleteAction.callback.invoke(position)
                true
            }
            else -> false
        }
    }

    override fun onClick(viewHolder: ViewHolder, position: Int) {}

    override fun onLongClick(viewHolder: MessagesAdapter.ViewHolder, position: Int) {
        PopupMenu(viewHolder.itemView.context, viewHolder.shape).apply {
            inflate(R.menu.fragment_messages_menu)
            setOnMenuItemClickListener { onMenuItemClick(it, viewHolder, position) }
            show()
        }
    }
}
