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
    private var isBothImagesVideos:Boolean = false

    private var permissionLauncher =
        (activity as AppCompatActivity).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissionList ->
            val allGranted = permissionList.all { it.value }
            if (allGranted)
                startMediaPicker(isMultiple,isVideo,isBothImagesVideos)
        }
    private var selectImage =
        (activity as AppCompatActivity).registerForActivityResult(
            SelectMediaContract(
                activity,
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
        isVideoOnly: Boolean,
        isBothImagesVideos:Boolean =false,
        listener: (List<Uri>, List<String>) -> Unit
    ) {
        this.onMediaPickedListener = listener
        this.isMultiple = isMultiple
        this.isVideo = isVideoOnly
        this.isBothImagesVideos =isBothImagesVideos
        if (PermissionUtils.isPermissionsGranted(activity, isVideo,isBothImagesVideos)) {
            startMediaPicker(isMultiple,isVideoOnly,isBothImagesVideos)
        } else {
            PermissionUtils.requestPermissions(activity, isVideo,isBothImagesVideos) { granted, list ->
                if (granted)
                    startMediaPicker(isMultiple,isVideoOnly,isBothImagesVideos)
                else
                    permissionLauncher.launch(list?.toTypedArray())
            }
        }
    }

    private fun startMediaPicker(isMultiple: Boolean,isVideoOnly: Boolean,isBothImagesVideos: Boolean) {
        val pickType = if (isBothImagesVideos) {
            listOf("image/*","video/*") }
        else if(isVideoOnly){
            listOf("video/*")
        } else {
            listOf("image/*")
        }
        selectImage.launch(Pair(pickType.toTypedArray(),isMultiple))
    }
}