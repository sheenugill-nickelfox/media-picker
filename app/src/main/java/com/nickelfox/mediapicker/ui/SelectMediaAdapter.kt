package com.nickelfox.mediapicker.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nickelfox.mediapicker.databinding.ListItemLayoutBinding
import java.io.File
import java.util.*

class SelectedMediaAdapter(var context: Context, var listener: ClickItem) :
    ListAdapter<File, SelectedMediaAdapter.ItemViewHolder>(SelectedItemsDiffUtil()) {

    inner class ItemViewHolder(private val binding: ListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaItems: File) {
            binding.apply {
                Glide.with(context).load(mediaItems.toUri()).into(itemSelectedTv)
                selectedPathTv.text = mediaItems.path
                val type = getMimeType(mediaItems.toUri())
                videoPlayBtn.isVisible = type == "video"
                itemSelectedTv.setOnClickListener {
                    type?.let { it1 -> listener.onClick(it1, mediaItems.toUri()) }
                }
            }
        }
    }

    private fun getMimeType(uri: Uri): String? {
        // var type= context.contentResolver.getType(uri)
        val type = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            type.lowercase(Locale.ROOT)
        )
        val split = mimeType?.split("/")?.toTypedArray()
        return split?.get(0)
    }

    class SelectedItemsDiffUtil : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.absolutePath == newItem.absolutePath
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem == newItem
        }
    }

    interface ClickItem {
        fun onClick(type: String, uri: Uri)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ListItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind(getItem(position))
}