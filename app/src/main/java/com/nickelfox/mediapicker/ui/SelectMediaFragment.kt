package com.nickelfox.mediapicker.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickelfox.media_picker.ui.MediaPicker
import com.nickelfox.mediapicker.R
import com.nickelfox.mediapicker.databinding.FragmentSelectMediaBinding
import java.io.File

class SelectMediaFragment : Fragment() {
    private var _binding: FragmentSelectMediaBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemAdapter: SelectedMediaAdapter
    private lateinit var mediaItemList: ArrayList<File>
    private lateinit var mediaPicker: MediaPicker

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
            mediaPicker = MediaPicker(requireActivity())
            itemAdapter =
                SelectedMediaAdapter(requireContext(), object : SelectedMediaAdapter.ClickItem {
                    override fun onClick(type: String, uri: Uri) {
                        ItemViewFragment(uri, type).show(childFragmentManager, getString(R.string.show_item))
                    }
                })
            itemRv.adapter = itemAdapter
            itemRv.layoutManager = LinearLayoutManager(requireContext())
            selectBtn.setOnClickListener {
                selectMedia(isMultiple = false, isVideo = true)
            }
            noSelectBtn.setOnClickListener {
                selectMedia(isMultiple = true, isVideo = false)
            }
            checkVisibility()
        }
    }

    private fun selectMedia(isMultiple: Boolean, isVideo: Boolean) {
        mediaPicker.pickMedia(isMultiple, isVideo) { mediaUris, mediaPaths ->
            val newItems = mediaPaths.map { File(it) }
            mediaItemList.addAll(newItems)
            itemAdapter.submitList(mediaItemList)
            itemAdapter.notifyDataSetChanged()
            checkVisibility()
        }
    }

    private fun checkVisibility() {
        binding.apply {
            noItemSelectedgroup.isVisible = itemAdapter.currentList.isEmpty()
            itemSelectedGroup.isVisible = itemAdapter.currentList.isNotEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
