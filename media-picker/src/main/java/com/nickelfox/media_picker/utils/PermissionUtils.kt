package com.nickelfox.media_picker.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

object PermissionUtils {
    fun isPermissionsGranted(context: Context, isVideo: Boolean): Boolean {
        val permissionList = requiredPermissions(isVideo).filter {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        return permissionList.size == requiredPermissions(isVideo).size
    }

    private fun requiredPermissions(isVideo: Boolean): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isVideo)
                arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
            else
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun requestPermissions(
        activity: Activity,
        isVideo: Boolean,
        callBack: (Boolean, List<String>?) -> Unit
    ) {
        val permissions = requiredPermissions(isVideo)
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_DENIED
        }
        if (notGrantedPermissions.isNotEmpty()) {
            val shouldShowRationale = notGrantedPermissions.all {
                activity.shouldShowRequestPermissionRationale(it)
            }
            if (shouldShowRationale) {
                AlertDialog.Builder(activity)
                    .setTitle("Media Permissions are required to Pick Media")
                    .setPositiveButton("Ok") { _, _ ->

                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            } else {
                callBack(false, notGrantedPermissions)
            }
        } else {
            callBack(true, null)
        }
    }
}