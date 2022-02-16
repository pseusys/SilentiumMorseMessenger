package com.ekdorn.silentium.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ekdorn.silentium.databinding.FragmentInputBinding


class InputFragment : Fragment() {
    private var _binding: FragmentInputBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentInputBinding.inflate(inflater, container, false).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
