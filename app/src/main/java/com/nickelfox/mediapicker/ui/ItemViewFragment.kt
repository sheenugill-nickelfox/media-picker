package com.nickelfox.mediapicker.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.nickelfox.mediapicker.databinding.FragmentItemViewBinding


class ItemViewFragment(private var uri: Uri, private var type: String) : DialogFragment() {

    private lateinit var binding: FragmentItemViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val mediaController = MediaController(requireContext())
            when (type) {
                "image" -> {
                    showImageItemIv.isVisible = true
                    showVideoItem.isVisible = false
                    showImageItemIv.setImageURI(uri)
                }
                "video" -> {
                    showImageItemIv.isVisible = false
                    showVideoItem.isVisible = true
                }
            }
            mediaController.setAnchorView(showVideoItem)
            showVideoItem.setVideoURI(uri)
            showVideoItem.setMediaController(mediaController)
            showVideoItem.start()
        }
    }
}