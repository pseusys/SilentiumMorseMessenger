package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekdorn.silentium.R
import com.ekdorn.silentium.databinding.FragmentDialogsBinding
import com.ekdorn.silentium.mvs.DialogsViewModel
import com.ekdorn.silentium.views.DescriptiveRecyclerView


class DialogsFragment : Fragment() {
    private lateinit var dialogsViewModel: DialogsViewModel
    private var _binding: FragmentDialogsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialogsViewModel = ViewModelProvider(this)[DialogsViewModel::class.java]

        _binding = FragmentDialogsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.dialogsView.initRecycler(DialogsAdapter(arrayOf("lol", "kekw")), LinearLayoutManager(requireContext()))

        //val textView: TextView = binding.textGallery
        //dialogsViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class DialogsAdapter(private val dialogs: Array<String>) : DescriptiveRecyclerView.Adapter<DialogsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val textView = view.findViewById<TextView>(R.id.textView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_dialog, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        //viewHolder.textView.text = dataSet[position]
    }

    override fun getItemCount() = dialogs.size
}
