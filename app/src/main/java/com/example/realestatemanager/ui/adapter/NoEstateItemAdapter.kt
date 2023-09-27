package com.example.realestatemanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realestatemanager.databinding.ItemNoEstateRecyclerBinding

class NoEstateItemAdapter(private val message: Int) :
    RecyclerView.Adapter<NoEstateItemAdapter.StringViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringViewHolder {
        val binding =
            ItemNoEstateRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StringViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StringViewHolder, position: Int) {
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return 1
    }

    inner class StringViewHolder(private val binding: ItemNoEstateRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Int) {
            binding.root.text = binding.root.context.getString(message)
        }
    }
}