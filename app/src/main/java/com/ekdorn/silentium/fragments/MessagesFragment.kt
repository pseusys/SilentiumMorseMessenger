package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekdorn.silentium.R
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.databinding.FragmentMessagesBinding
import com.ekdorn.silentium.mvs.MessagesViewModel
import com.ekdorn.silentium.visuals.VisualAction
import com.ekdorn.silentium.adapters.MessagesAdapter
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.models.Contact


class MessagesFragment : Fragment() {
    private lateinit var messagesViewModel: MessagesViewModel
    private var _binding: FragmentMessagesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        messagesViewModel = ViewModelProvider(requireActivity())[MessagesViewModel::class.java].initContact(Contact("123", null, "88005553535", null, null, null))
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        binding.sendButton.setOnClickListener {
            val text = binding.messageInput.text.toString()
            if (text.isNotBlank()) {
                messagesViewModel.addMessage(text.toMyteReadable(), UserManager[requireContext()].value!!)
                binding.messageInput.text.clear()
            }
        }

        val deleteAction = VisualAction(R.drawable.icon_delete, R.color.red, R.color.white, IntRange.EMPTY) { messagesViewModel.removeMessage(it) }
        val adapter = MessagesAdapter(requireContext(), UserManager[requireContext()].value!!, deleteAction)
        val recycler = binding.messagesView.initRecycler(adapter, LinearLayoutManager(requireContext()))

        messagesViewModel.messages.observe(viewLifecycleOwner) {
            Log.e("TAG", "onCreateView: $it")
            deleteAction.views = it.indices
            adapter.sync(it)
            if (it.isNotEmpty()) recycler.smoothScrollToPosition(it.size - 1)
        }
        UserManager[requireContext()].observe(viewLifecycleOwner) {
            adapter.me = it
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
