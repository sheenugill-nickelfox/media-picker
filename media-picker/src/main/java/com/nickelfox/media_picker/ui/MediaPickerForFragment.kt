package com.nickelfox.media_picker.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.nickelfox.media_picker.R
import com.nickelfox.media_picker.utils.PermissionUtils
import java.io.File

class MediaPickerForFragment(private val fragment: Fragment) {
    private var onMediaPickedListener: ((List<Uri>, List<String>) -> Unit)? = null
    private var isMultiple: Boolean = false
    private var isVideo: Boolean = false
    private var isBothImagesVideos: Boolean = false
    private var showLongPressInstructDialog = true
    private var isCropped: Boolean = false
    private var onCroppedImageListener: ((Bitmap,File) -> Unit)? = null

    private var permissionLauncher =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionList ->
            val allGranted = permissionList.all { it.value }
            if (allGranted)
                startMediaPicker(isMultiple, isVideo, isBothImagesVideos)
        }
    private var selectImage =
        fragment.registerForActivityResult(
            SelectMediaContract(
                fragment.requireContext(),
                isVideo
            )
        ) {
            it?.first?.let { it1 ->
                if (isCropped) {
                    continueCroppingImage(it1[0])
                    isCropped = false
                } else {
                    it.second?.let { it2 ->
                        onMediaPickedListener?.invoke(
                            it1,
                            it2
                        )
                    }
                }
            }
        }

    fun pickMedia(
        isMultiple: Boolean,
        isVideoOnly: Boolean,
        isBothImagesVideos: Boolean = false,
        listener: (List<Uri>, List<String>) -> Unit
    ) {
        this.onMediaPickedListener = listener
        this.isMultiple = isMultiple
        this.isVideo = isVideoOnly
        this.isBothImagesVideos = isBothImagesVideos
        if (PermissionUtils.isPermissionsGranted(
                fragment.requireContext(),
                isVideo,
                isBothImagesVideos
            )
        ) {
            startMediaPicker(isMultiple, isVideoOnly, isBothImagesVideos)
        } else {
            PermissionUtils.requestPermissions(
                fragment.requireActivity(),
                isVideo,
                isBothImagesVideos
            ) { granted, list ->
                if (granted)
                    startMediaPicker(isMultiple, isVideoOnly, isBothImagesVideos)
                else
                    permissionLauncher.launch(list?.toTypedArray())
            }
        }
    }
    private fun continueCroppingImage(uri: Uri) {
        ImageCropFragment
            .newInstance(uri = uri)
            .setOnCropSuccessListener(onCroppedImageListener)
            .show(fragment.childFragmentManager, null)
    }

    fun pickAndCropImage(listener: (Bitmap,File) -> Unit) {
        this.onCroppedImageListener = listener
        this.isCropped = true
        if (PermissionUtils.isPermissionsGranted(fragment.requireContext(),
                isVideo = false,
                isBoth = false
            )) {
            startMediaPicker(isMultiple = false, isVideoOnly = false, isBothImagesVideos = false)
        } else {
            PermissionUtils.requestPermissions(
                fragment.requireActivity(),
                isVideo,
                isBothImagesVideos
            ) { granted, list ->
                if (granted)
                    startMediaPicker(
                        isMultiple = false,
                        isVideoOnly = false,
                        isBothImagesVideos = false
                    )
                else
                    permissionLauncher.launch(list?.toTypedArray())
            }
        }

    }

    private fun startMediaPicker(
        isMultiple: Boolean,
        isVideoOnly: Boolean,
        isBothImagesVideos: Boolean
    ) {
        val pickType = if (isBothImagesVideos) {
            listOf("image/*", "video/*")
        } else if (isVideoOnly) {
            listOf("video/*")
        } else {
            listOf("image/*")
        }
        if (isMultiple && showLongPressInstructDialog)
            showAlertDialog(pickType)
        else
            selectImage.launch(Pair(pickType.toTypedArray(), isMultiple))

    }

    private fun showAlertDialog(pickType: List<String>) {
        val builder = AlertDialog.Builder(fragment.requireContext())
            .setTitle(fragment.requireContext().getString(R.string.multiple_selection))
            .setMessage(fragment.requireContext().getString(R.string.long_press))
            .setPositiveButton(
                fragment.requireContext().getString(R.string.ok)
            ) { _, _ -> selectImage.launch(Pair(pickType.toTypedArray(), true)) }
        builder.create().show()
        showLongPressInstructDialog = false
    }
}