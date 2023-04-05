package com.nickelfox.media_picker.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.nickelfox.media_picker.databinding.FragmentImageCropBinding

class ImageCropFragment : DialogFragment() {
    private var binding: FragmentImageCropBinding? = null
    private var onSuccess: ((Bitmap) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageCropBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            val uri = Uri.parse(arguments?.getString(IMAGE_URI))
            cropImageView.setImageUriAsync(uri)
            initListeners()
        }
    }

    private fun initListeners() {
        binding?.apply {
            save.setOnClickListener {
                cropImageView.croppedImage?.let {
                    onSuccess?.invoke(it)
                }
                dismiss()
            }
            cancel.setOnClickListener {
                dismiss()
            }
        }
    }

    fun setOnCropSuccessListener(onSuccess: ((Bitmap) -> Unit)?): ImageCropFragment {
        this.onSuccess = onSuccess
        return this
    }

    companion object{
        private const val IMAGE_URI = "IMAGE_URI"
        fun newInstance(uri: Uri):ImageCropFragment{
            val fragment = ImageCropFragment()
            fragment.arguments = bundleOf(IMAGE_URI to uri.toString())
            return fragment
        }
    }
}