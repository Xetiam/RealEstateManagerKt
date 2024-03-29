package com.example.realestatemanager.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.realestatemanager.R
import com.example.realestatemanager.Utils
import com.example.realestatemanager.databinding.ItemEstateRecyclerBinding
import com.example.realestatemanager.model.EstateModel
import com.example.realestatemanager.ui.settings.SettingsFragment

class EstateItemAdapter(
    private val selectedEstate: Long?,
    private val context: Context,
    private val callback: (Long?) -> Unit
) :
    ListAdapter<EstateModel, EstateItemAdapter.EstateViewHolder>(EstateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstateViewHolder {
        val binding: ItemEstateRecyclerBinding =
            ItemEstateRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EstateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EstateViewHolder, position: Int) {
        val estate = getItem(position)
        holder.bind(estate)
    }

    inner class EstateViewHolder(private val binding: ItemEstateRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(estate: EstateModel) {
            val sharedPrefs =
                context.getSharedPreferences(SettingsFragment.USER_PREFS, Context.MODE_PRIVATE)
            val currency = sharedPrefs.getString(SettingsFragment.USER_CURRENCY, "USD")
            val price: String
            price = if (currency == "USD") {
                "$${Utils.formatPriceNumber(estate.dollarPrice)}"
            } else {
                "${Utils.formatPriceNumber(Utils.convertDollarToEuro(estate.dollarPrice))} €"
            }
            binding.apply {
                estateType.text = estate.type.label
                estateCity.text = Utils.extractCityFromAddress(estate.address)
                estatePrice.text = price
                if (selectedEstate == estate.id) {
                    root.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.purple_500)
                    )
                } else {
                    root.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.white)
                    )
                }
                root.setOnClickListener {
                    callback(estate.id)
                }
                val uri = estate.pictures[0].first
                loadImageWithGlide(uri)
            }

        }

        private fun loadImageWithGlide(uri: Uri) {
            Glide.with(binding.root.context)
                .load(uri)
                .placeholder(R.drawable.ic_gallery_black_24dp)
                .centerCrop()
                .into(binding.estatePicture)
        }
    }
}

class EstateDiffCallback : DiffUtil.ItemCallback<EstateModel>() {
    override fun areItemsTheSame(oldItem: EstateModel, newItem: EstateModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EstateModel, newItem: EstateModel): Boolean {
        return oldItem == newItem
    }
}
