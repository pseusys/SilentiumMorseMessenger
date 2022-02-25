package com.ekdorn.silentium.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.core.toReadableString
import com.ekdorn.silentium.databinding.FragmentMessagesBinding
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.models.Message
import com.ekdorn.silentium.mvs.MessagesViewModel
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
            messagesViewModel.addMessage(binding.messageInput.text.toString().toMyteReadable())
            binding.messageInput.text.clear()
        }

        val adapter = MessagesAdapter(requireContext(), emptyList())
        binding.messagesView.initRecycler(adapter, LinearLayoutManager(requireContext()))

        messagesViewModel.messages.observe(viewLifecycleOwner) { adapter.sync(it) }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class MessagesAdapter(context: Context, private var messages: List<Message>) : DescriptiveRecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    private val incomingMessage = ContextCompat.getDrawable(context, R.drawable.shape_message_in)
    private val outcomingMessage = ContextCompat.getDrawable(context, R.drawable.shape_message_out)

    private val incomingText = ContextCompat.getColor(context, R.color.white)
    private val outcomingText = ContextCompat.getColor(context, R.color.black)

    private val messageFrontOffset = context.resources.getDimension(R.dimen.message_front_offset).toInt()
    private val messageBackOffset = context.resources.getDimension(R.dimen.message_back_offset).toInt()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val root: ConstraintLayout = view.findViewById(R.id.root)
        private val shape: CardView = view.findViewById(R.id.message_shape)
        private val holder: ConstraintLayout = view.findViewById(R.id.message_holder)
        val textView: TextView = view.findViewById(R.id.text_view)
        val dateTime: TextClock = view.findViewById(R.id.date_time_view)

        fun configureMessage(incoming: Boolean = false) {
            val constraintSet = ConstraintSet()
            val color: Int
            constraintSet.clone(root)
            if (incoming) {
                color = incomingText
                shape.background = incomingMessage!!.constantState!!.newDrawable().mutate()
                constraintSet.connect(R.id.message_shape, ConstraintSet.LEFT, R.id.root, ConstraintSet.LEFT, 0)
                (holder.layoutParams as FrameLayout.LayoutParams).updateMargins(left = messageBackOffset, right = messageFrontOffset)
            } else {
                color = outcomingText
                shape.background = outcomingMessage!!.constantState!!.newDrawable().mutate()
                constraintSet.connect(R.id.message_shape, ConstraintSet.RIGHT, R.id.root, ConstraintSet.RIGHT, 0)
                (holder.layoutParams as FrameLayout.LayoutParams).updateMargins(left = messageFrontOffset, right = messageBackOffset)
            }
            constraintSet.applyTo(root)
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
    }

    override fun getItemCount() = messages.size

    override fun onClick(viewHolder: ViewHolder, position: Int) {}

    override fun onLongClick(viewHolder: ViewHolder, position: Int) {}
}

