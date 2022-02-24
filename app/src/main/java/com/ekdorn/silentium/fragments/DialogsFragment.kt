package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.*
import com.ekdorn.silentium.databinding.FragmentDialogsBinding
import com.ekdorn.silentium.models.Dialog
import com.ekdorn.silentium.mvs.DialogsViewModel
import com.ekdorn.silentium.views.DescriptiveRecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.imageview.ShapeableImageView


class DialogsFragment : Fragment() {
    private lateinit var dialogsViewModel: DialogsViewModel
    private var _binding: FragmentDialogsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialogsViewModel = ViewModelProvider(requireActivity())[DialogsViewModel::class.java]
        _binding = FragmentDialogsBinding.inflate(inflater, container, false)

        dialogsViewModel.getDialogs()

        val adapter = DialogsAdapter(emptyList())
        binding.dialogsView.initRecycler(adapter, LinearLayoutManager(requireContext()))

        dialogsViewModel.dialogs.observe(viewLifecycleOwner) { adapter.sync(it) }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class DialogsAdapter(private var dialogs: List<Dialog>) : DescriptiveRecyclerView.Adapter<DialogsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
                return dialogs[oip].messages.last() == new[nip].messages.last()
            }
            override fun areContentsTheSame(oip: Int, nip: Int): Boolean {
                val o = dialogs[oip].messages.last()
                val n = new[nip].messages.last()
                return (o.text.contentEquals(n.text)) && (o.date == n.date) && (o.author == n.author) && (o.read == n.read)
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
        viewHolder.contactName.text = dialogs[position].contact.name ?: dialogs[position].contact.contact
        viewHolder.lastMessage.text = dialogs[position].messages.last().text.toReadableString()
        val unread = dialogs[position].messages.count { !it.read }
        if (unread > 0) {
            viewHolder.unreadCount.visibility = View.VISIBLE
            viewHolder.unreadCount.text = unread.toString()
        } else viewHolder.unreadCount.visibility = View.INVISIBLE
    }

    override fun getItemCount() = dialogs.size
}
