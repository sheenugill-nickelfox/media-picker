package com.nickelfox.media_picker.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority

}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Drive uri.
 */
fun isDriveUri(uri: Uri): Boolean {
    return "com.google.android.apps.docs.storage" == uri.authority ||
            "com.google.android.apps.docs.storage.legacy" == uri.authority
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param context The context.
 * @param uri The Uri to query.
 * @param selection (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
fun getDataColumn(
    context: Context, uri: Uri, selection: String?,
    selectionArgs: Array<String?>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(
        column
    )
    try {
        cursor = context.contentResolver.query(
            uri, null, selection, selectionArgs, null
        )
        if (cursor != null  && cursor.moveToFirst()) {
            val index: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

/**
 * Method for return file path of Gallery image
 *
 * @param context
 * @param uri
 * @return path of the selected image file from gallery
 */
fun getPath(context: Context?, uri: Uri): String? {
    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            return if ("primary".equals(type, ignoreCase = true)) {
                Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else{
                val externalDirec = context?.getExternalFilesDirs(null)
                if (externalDirec!!.size > 1){
                    var externalpath = externalDirec[1].absolutePath
                    externalpath=externalpath.substring(0,externalpath.indexOf("Android")) + split[1]
                    if(File(externalpath).exists()){
                        externalpath
                    }else{
                        getFile(context, uri)
                    }
                }else {
                    getFile(context, uri)
                }
            }
        } else if (isDownloadsDocument(uri)) {

            val filepath= getFilepath(context,uri)
            val path= Environment.getExternalStorageDirectory().toString() + "/Download/" +filepath
            if(File(path).exists()) {
                return path}
            val id = DocumentsContract.getDocumentId(uri)
            val split = id.split(":").toTypedArray()
            val type = split[0]
            if("msf".equals(type,ignoreCase = true)) {
                getFile(context,uri)
            } else{
                val contentUri=  ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context!!, contentUri, null,null)
            }
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                "video" -> {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                "audio" -> {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf<String?>(
                split[1]
            )
            return getDataColumn(context!!, contentUri!!, selection, selectionArgs)
        } else if(isDriveUri(uri)){
            getFile(context,uri)
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)){
                uri.lastPathSegment }
            else {
                getDataColumn(
                    context!!,
                    uri,
                    null,
                    null
                )
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
    }
    return null
}

fun getFilepath(context: Context?, uri: Uri): String {
    val returnCursor =
        context?.contentResolver?.query(uri, null, null, null, null)
    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name=returnCursor.getString(nameIndex)
    returnCursor.close()
    return name
}

fun getFile(context: Context?, uri: Uri): String? {
    val filepath= getFilepath(context,uri)
    try{
        val file = File(context?.cacheDir, filepath)
        val inputStream =
            context?.contentResolver?.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        if (inputStream != null) {
            var read: Int
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream.available()
            val bufferSize = min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
        }
        inputStream?.close()
        outputStream.close()
        return file.path
    }catch (e: Exception) {
        return null
    }
}
