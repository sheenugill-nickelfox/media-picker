package com.nickelfox.media_picker.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.canhub.cropper.CropImageView
import com.nickelfox.media_picker.databinding.FragmentImageCropBinding
import com.nickelfox.media_picker.utils.getFileFromBitmap
import java.io.File

enum class ImageShape {
    OVAL,RECTANGLE
}

class ImageCropFragment : DialogFragment() {
    private var binding: FragmentImageCropBinding? = null
    private var onSuccess: ((Bitmap,File) -> Unit)? = null

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
            cropImageView.cropShape = arguments?.getString(
                IMAGE_SHAPE)?.let { CropImageView.CropShape.valueOf(it) }
            cropImageView.setImageUriAsync(uri)
            initListeners()
        }
    }

    private fun initListeners() {
        binding?.apply {
            save.setOnClickListener {
                cropImageView.croppedImage?.let {
                    onSuccess?.invoke(redrawBitmap(it),requireContext().getFileFromBitmap(redrawBitmap(it)))
                }
                dismiss()
            }
            cancel.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun redrawBitmap(bitmap:Bitmap):Bitmap{
        val whiteBackgroundBitmap = Bitmap.createBitmap(
            bitmap.width, bitmap.height, bitmap.config
        )
        val canvas = Canvas(whiteBackgroundBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return whiteBackgroundBitmap
    }

    fun setOnCropSuccessListener(onSuccess: ((Bitmap, File) -> Unit)?): ImageCropFragment {
        this.onSuccess = onSuccess
        return this
    }

    companion object{
        private const val IMAGE_URI = "IMAGE_URI"
        private const val IMAGE_SHAPE = "IMAGE_SHAPE"
        fun newInstance(uri: Uri,shape:ImageShape):ImageCropFragment{
            val fragment = ImageCropFragment()
            fragment.arguments = bundleOf(IMAGE_URI to uri.toString(),
            IMAGE_SHAPE to shape.name)
            return fragment
        }
    }
}