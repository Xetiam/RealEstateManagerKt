package com.example.realestatemanager.ui.adapter

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.realestatemanager.databinding.ItemEstatePictureBinding

class EstatePictureItemAdapter(
    private val imageUris: List<Uri>,
    private val descriptionsOld: List<String>?,
    private val callback: (String, Int) -> Unit
) : PagerAdapter() {
    val descriptions: MutableList<String?> = MutableList(imageUris.size) { null }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val binding = ItemEstatePictureBinding.inflate(inflater)
        if (descriptions[position].isNullOrEmpty()) {
            binding.description.setText("")
        } else {
            binding.description.setText(descriptions[position])
        }
        Glide.with(binding.root.context)
            .load(imageUris[position])
            .centerCrop()
            .into(binding.picture)
        descriptionsOld?.let {
            if (descriptionsOld.size > position) {
                binding.description.setText(descriptionsOld[position])
            }
        }
        binding.description.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callback(s.toString(), position)
                descriptions[position] = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int = imageUris.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
}