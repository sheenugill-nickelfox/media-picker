package com.nickelfox.mediapicker.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickelfox.mediapicker.databinding.FragmentSelectMediaBinding
import java.io.File

class SelectMediaFragment : Fragment() {
    private var _binding: FragmentSelectMediaBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemAdapter: SelectedMediaAdapter
    private lateinit var mediaItemList: ArrayList<File>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            mediaItemList = ArrayList()
            selectBtn.setOnClickListener {

            }
            noSelectBtn.setOnClickListener {

            }

            itemAdapter =
                SelectedMediaAdapter(requireContext(), object : SelectedMediaAdapter.ClickItem {
                    override fun onClick(type: String, uri: Uri) {
                        ItemViewFragment(uri, type).show(childFragmentManager, "Showitem")
                    }
                })
            itemRv.adapter = itemAdapter
            itemRv.layoutManager = LinearLayoutManager(requireContext())
            checkVisibility()
        }
    }

    private fun checkVisibility() {
        binding.apply {
            if (itemAdapter.currentList.size > 0) {
                noItemSelectedgroup.isVisible = false
                itemSelectedGroup.isVisible = true
            } else {
                noItemSelectedgroup.isVisible = true
                itemSelectedGroup.isVisible = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
