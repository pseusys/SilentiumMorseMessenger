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
import com.ekdorn.silentium.databinding.FragmentNotesBinding
import com.ekdorn.silentium.models.Note
import com.ekdorn.silentium.mvs.NotesViewModel
import com.ekdorn.silentium.views.DescriptiveRecyclerView


class NotesFragment : Fragment() {
    private lateinit var notesViewModel: NotesViewModel
    private var _binding: FragmentNotesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        notesViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        _binding = FragmentNotesBinding.inflate(inflater, container, false)

        val adapter = NotesAdapter(emptyList())
        binding.notesView.initRecycler(adapter, LinearLayoutManager(requireContext()))

        notesViewModel.notes.observe(viewLifecycleOwner) { adapter.sync(it) }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class NotesAdapter(private var notes: List<Note>) : DescriptiveRecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTime: TextClock = view.findViewById(R.id.date_time_view)
        val text: TextView = view.findViewById(R.id.text_view)
    }

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
                return (o.text.contentEquals(n.text)) && (o.date == n.date)
            }
        })
        notes = new
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_note, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        viewHolder.dateTime.text = notes[position].date.toString()
        viewHolder.text.text = notes[position].text.toReadableString()
    }

    override fun getItemCount() = notes.size

    override fun onClick(viewHolder: ViewHolder, position: Int) {}

    override fun onLongClick(viewHolder: ViewHolder, position: Int) {}
}
