package com.nickelfox.media_picker.ui

import android.app.Activity
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.nickelfox.media_picker.utils.PermissionUtils

class MediaPicker(private val activity: Activity) {
    private var onMediaPickedListener: ((List<Uri>, List<String>) -> Unit)? = null
    private var isMultiple: Boolean = false
    private var isVideo: Boolean = false

    private var permissionLauncher =
        (activity as AppCompatActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionList ->
            val allGranted = permissionList.all { it.value }
            if (allGranted)
                startMediaPicker(isVideo)
        }
    private var selectImage =
        (activity as AppCompatActivity).registerForActivityResult(
            SelectMediaContract(
                activity,
                isMultiple,
                isVideo
            )
        ) {
            it?.first?.let { it1 ->
                it.second?.let { it2 ->
                    onMediaPickedListener?.invoke(
                        it1,
                        it2
                    )
                }
            }
        }

    fun pickMedia(
        isMultiple: Boolean,
        isVideo: Boolean,
        listener: (List<Uri>, List<String>) -> Unit
    ) {
        this.onMediaPickedListener = listener
        this.isMultiple = isMultiple
        this.isVideo = isVideo
        if (PermissionUtils.isPermissionsGranted(activity, isVideo)) {
            startMediaPicker(isVideo)
        } else {
            PermissionUtils.requestPermissions(activity, isVideo) { granted, list ->
                if (granted)
                    startMediaPicker(isVideo)
                else
                    permissionLauncher.launch(list?.toTypedArray())
            }
        }
    }

    private fun startMediaPicker(isVideo: Boolean) {
        val pickType = if (isVideo) arrayOf("video/*") else arrayOf("image/*")
        selectImage.launch(pickType)
    }
}