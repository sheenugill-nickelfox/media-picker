package com.nickelfox.media_picker.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.nickelfox.media_picker.utils.getPath

class SelectMediaContract(private val context: Context, private val isVideo:Boolean):
    ActivityResultContract<Pair<Array<String>,Boolean>, Pair<ArrayList<Uri>?, ArrayList<String>?>?>() {

    private lateinit var uri: Uri
    private var mediaUriList:ArrayList<Uri>? = null
    private var mediaPathList:ArrayList<String>? = null

    override fun createIntent(context: Context, input: Pair<Array<String>,Boolean>): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = if (isVideo) "video/*" else "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, input.first)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,input.second)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<ArrayList<Uri>?, ArrayList<String>?>? {
        mediaUriList=ArrayList()
        mediaPathList=ArrayList()
        if(intent==null|| resultCode!= Activity.RESULT_OK ) {
            return null
        }
        else{
            if(intent.clipData != null){
                val count=intent.clipData?.itemCount
                for(i in 0 until count!!){
                    uri= (intent.clipData?.getItemAt(i)?.uri  ?: "") as Uri
                    mediaUriList?.add(uri)
                    getPath(context,uri)?.let { mediaPathList?.add(it) }
                }
            }else{
                intent.data?.let {
                    mediaUriList?.add(it)
                    getPath(context,it)?.let { it1 -> mediaPathList?.add(it1) }
                }
            }
            return Pair(mediaUriList,mediaPathList)
        }
    }
}