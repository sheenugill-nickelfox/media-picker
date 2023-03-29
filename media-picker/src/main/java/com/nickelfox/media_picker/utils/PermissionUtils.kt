package com.nickelfox.media_picker.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.nickelfox.media_picker.R

object PermissionUtils {
    fun isPermissionsGranted(context: Context, isVideo: Boolean,isBoth:Boolean): Boolean {
        val permissionList = requiredPermissions(isVideo,isBoth).filter {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        return permissionList.size == requiredPermissions(isVideo,isBoth).size
    }

    private fun requiredPermissions(isVideo: Boolean,isBoth: Boolean): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isBoth) {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
            }else if(isVideo){
                arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
            }
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
        isBoth: Boolean,
        callBack: (Boolean, List<String>?) -> Unit
    ) {
        val permissions = requiredPermissions(isVideo,isBoth)
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_DENIED
        }
        if (notGrantedPermissions.isNotEmpty()) {
            val shouldShowRationale = notGrantedPermissions.all {
                activity.shouldShowRequestPermissionRationale(it)
            }
            if (shouldShowRationale) {
                showAlertDialog(activity)
            } else {
                callBack(false, notGrantedPermissions)
            }
        } else {
            callBack(true, null)
        }
    }
    private fun showAlertDialog(context: Context){
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.enable_permissions_title))
            .setMessage(context.getString(R.string.enable_permissions_body))
            .setPositiveButton(context.getString(R.string.setting)) { dialog, _ ->
                Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", context.packageName, null)
                    data = uri
                    dialog.dismiss()
                    context.startActivity(this)
                }

            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}