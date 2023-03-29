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
            selectImageBtn.setOnClickListener {
                selectMedia(isMultiple = false, isVideoOnly = false)
            }
            selectVideoBtn.setOnClickListener {
                selectMedia(isMultiple = false, isVideoOnly = true)
            }
            selectMultipleImagesBtn.setOnClickListener {
                selectMedia(isMultiple = true, isVideoOnly = false)
            }
            selectMultipleVideosBtn.setOnClickListener {
                selectMedia(isMultiple = true, isVideoOnly = true)
            }
            noSelectBtn.setOnClickListener {
                selectMedia(isMultiple = true, isVideoOnly = false,isBoth = true)
            }
            selectBothImagesVideos.setOnClickListener {
                selectMedia(isMultiple = true, isVideoOnly = false,isBoth = true)
            }
            checkVisibility()
            selectedCount()
        }
    }

    private fun selectMedia(isMultiple: Boolean, isVideoOnly: Boolean,isBoth:Boolean = false) {
        mediaPicker.pickMedia(isMultiple, isVideoOnly,isBoth) { mediaUris, mediaPaths ->
            mediaPaths.forEach {
                mediaItemList.add(File(it))
            }
            itemAdapter.submitList(mediaItemList)
            itemAdapter.notifyDataSetChanged()
            checkVisibility()
            selectedCount()
        }
    }

    private fun checkVisibility() {
        binding.apply {
            noItemSelectedGroup.isVisible = itemAdapter.currentList.isEmpty()
            itemSelectedGroup.isVisible = itemAdapter.currentList.isNotEmpty()
        }
    }
    private fun selectedCount(){
        binding.selectedCountTv.text = buildString {
            append("(${itemAdapter.currentList.size})")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
