package com.example.realestatemanager.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ItemEstatePictureDetailBinding

class EstatePictureDetailItemAdapter :
    ListAdapter<Pair<Uri, String>, EstatePictureDetailItemAdapter.UriStringViewHolder>(
        UriStringDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UriStringViewHolder {
        val binding: ItemEstatePictureDetailBinding =
            ItemEstatePictureDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UriStringViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: UriStringViewHolder, position: Int) {
        val (uri, text) = getItem(position)
        holder.bind(uri, text)
    }

    inner class UriStringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(uri: Uri, text: String) {
            val binding = ItemEstatePictureDetailBinding.bind(itemView)
            binding.apply {
                Glide.with(itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.ic_gallery_black_24dp)
                    .centerCrop()
                    .into(estatePictureDetail)
                photoDescription.text = text
            }
        }
    }

    class UriStringDiffCallback : DiffUtil.ItemCallback<Pair<Uri, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<Uri, String>,
            newItem: Pair<Uri, String>
        ): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(
            oldItem: Pair<Uri, String>,
            newItem: Pair<Uri, String>
        ): Boolean {
            return oldItem == newItem
        }
    }
}