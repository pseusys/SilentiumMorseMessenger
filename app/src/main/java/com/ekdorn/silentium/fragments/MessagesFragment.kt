package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekdorn.silentium.R
import com.ekdorn.silentium.adapters.MessagesAdapter
import com.ekdorn.silentium.core.toMyteReadable
import com.ekdorn.silentium.databinding.FragmentMessagesBinding
import com.ekdorn.silentium.managers.NetworkManager
import com.ekdorn.silentium.managers.PrivilegeManager
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.models.Payload
import com.ekdorn.silentium.mvs.ContactsViewModel
import com.ekdorn.silentium.mvs.MessageViewModelFactory
import com.ekdorn.silentium.mvs.MessagesViewModel
import com.ekdorn.silentium.visuals.VisualAction
import kotlinx.coroutines.launch


class MessagesFragment : Fragment(), HasDefaultViewModelProviderFactory {
    private val contactsViewModel by activityViewModels<ContactsViewModel>()
    private val messagesViewModel by viewModels<MessagesViewModel>()
    private val args: MessagesFragmentArgs by navArgs()

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        lifecycleScope.launch { contactsViewModel.addContact(NetworkManager.updateContact(args.contact)) }

        if (args.contact in PrivilegeManager.ACCOUNTS) binding.inputView.visibility = View.GONE
        else binding.inputView.setOnClickListener {
            val text = binding.inputView.text.toString()
            if (text.isNotBlank()) {
                val myte = text.toMyteReadable(binding.root.context)
                lifecycleScope.launch { NetworkManager.sendMessage(requireContext(), args.contact, Payload(myte, binding.inputView.morse as CharSequence)) }
                messagesViewModel.addMessage(myte, UserManager[requireContext()].value!!, binding.inputView.morse)
                binding.inputView.text.clear()
            }
        }

        val deleteAction = VisualAction(R.drawable.icon_delete, R.color.red, R.color.white, IntRange.EMPTY) { messagesViewModel.removeMessage(it) }
        val adapter = MessagesAdapter(requireContext(), UserManager[requireContext()].value!!, deleteAction)
        val recycler = binding.messagesView.initRecycler(adapter, LinearLayoutManager(requireContext()))

        recycler.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if ((bottom < oldBottom) && (adapter.itemCount > 0)) recycler.smoothScrollToPosition(adapter.itemCount - 1)
        }

        messagesViewModel.messages.observe(viewLifecycleOwner) {
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

    fun onBackPressed() = binding.inputView.onBackPressed()

    override fun getDefaultViewModelProviderFactory() = MessageViewModelFactory(requireActivity().application, args.contact)
}
