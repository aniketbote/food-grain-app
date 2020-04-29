package com.example.farmfresh

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

class Adapter(models:MutableList<String>, context: Context) : PagerAdapter() {
    private val models: MutableList<String> = models
    private val layoutInflater: LayoutInflater ?= null
    private val context:Context = context


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    override fun getCount(): Int {
        return models.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position)
        layoutInflater = LayoutInflater.from(context)
        val view:View = layoutInflater.inflate(R.layout.item, container, false)
        val imageView:ImageView = view.findViewById(R.id.image_index)
        val title:TextView = view.findViewById(R.id.text)

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        container.removeView(`object` as View)
    }
}