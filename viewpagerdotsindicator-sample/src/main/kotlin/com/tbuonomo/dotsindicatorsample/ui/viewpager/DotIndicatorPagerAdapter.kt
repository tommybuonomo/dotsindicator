package com.tbuonomo.dotsindicatorsample.ui.viewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.tbuonomo.dotsindicatorsample.R

class DotIndicatorPagerAdapter : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = LayoutInflater.from(container.context).inflate(
            R.layout.material_page, container,
            false
        )
        container.addView(item)
        return item
    }

    override fun getCount(): Int {
        return 10
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
