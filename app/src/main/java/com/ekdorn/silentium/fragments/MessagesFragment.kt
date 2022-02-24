package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextClock
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.toReadableString
import com.ekdorn.silentium.databinding.FragmentMessagesBinding
import com.ekdorn.silentium.models.Message
import com.ekdorn.silentium.mvs.MessagesViewModel
import com.ekdorn.silentium.views.DescriptiveRecyclerView


class MessagesFragment : Fragment() {
    private lateinit var messagesViewModel: MessagesViewModel
    private var _binding: FragmentMessagesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        messagesViewModel = ViewModelProvider(this)[MessagesViewModel::class.java]
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        // TODO: remove
        messagesViewModel.getMessages()

        val adapter = MessagesAdapter(emptyList())
        binding.messagesView.initRecycler(adapter, LinearLayoutManager(requireContext()))

        messagesViewModel.messages.observe(viewLifecycleOwner) { adapter.sync(it) }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class MessagesAdapter(private var messages: List<Message>) : DescriptiveRecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view)
        val dateTime: TextClock = view.findViewById(R.id.date_time_view)
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
        viewHolder.dateTime.text = messages[position].date.toString()
        viewHolder.textView.text = messages[position].text.toReadableString()
    }

    override fun getItemCount() = messages.size

    override fun onClick(viewHolder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onLongClick(viewHolder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}

