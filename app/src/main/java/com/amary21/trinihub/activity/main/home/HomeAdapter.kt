package com.amary21.trinihub.activity.main.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.amary21.trinihub.R
import com.amary21.trinihub.data.local.FanArt
import kotlinx.android.synthetic.main.item_fanart.view.*

class HomeAdapter(private val context: Context) : PagerAdapter() {

    private var fanart: MutableList<FanArt> = ArrayList()

    fun setImage(fanart: List<FanArt>) {
        this.fanart.addAll(fanart)
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return fanart.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_fanart, container, false)

        fanart[position].image?.let { view.imgFanart.setImageDrawable(it) }

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}