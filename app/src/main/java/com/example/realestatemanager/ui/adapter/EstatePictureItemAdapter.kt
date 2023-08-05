package com.example.realestatemanager.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.realestatemanager.R

class EstatePictureItemAdapter(private val imageUris: List<Uri>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val imageView = inflater.inflate(R.layout.item_estate_picture, container, false) as ImageView
        imageView.setImageURI(imageUris[position])
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int = imageUris.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
}