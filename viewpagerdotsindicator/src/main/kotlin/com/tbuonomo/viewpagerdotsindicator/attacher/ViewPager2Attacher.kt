package com.tbuonomo.viewpagerdotsindicator.attacher

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper
import com.tbuonomo.viewpagerdotsindicator.isEmpty
import com.tbuonomo.viewpagerdotsindicator.isNotEmpty

internal class ViewPager2Attacher :
    DotsIndicatorAttacher<ViewPager2, RecyclerView.Adapter<*>>() {
    override fun getAdapterFromAttachable(attachable: ViewPager2): RecyclerView.Adapter<*>? =
        attachable.adapter

    override fun registerAdapterDataChangedObserver(
        attachable: ViewPager2,
        adapter: RecyclerView.Adapter<*>,
        onChanged: () -> Unit
    ) {
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                onChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                onChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                onChanged()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                onChanged()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                onChanged()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                onChanged()
            }
        })
    }

    override fun buildPager(
        attachable: ViewPager2,
        adapter: RecyclerView.Adapter<*>
    ): BaseDotsIndicator.Pager {
        return object : BaseDotsIndicator.Pager {
            var onPageChangeCallback: ViewPager2.OnPageChangeCallback? = null

            override val isNotEmpty: Boolean
                get() = attachable.isNotEmpty
            override val currentItem: Int
                get() = attachable.currentItem
            override val isEmpty: Boolean
                get() = attachable.isEmpty
            override val count: Int
                get() = attachable.adapter?.itemCount ?: 0

            override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
                attachable.setCurrentItem(item, smoothScroll)
            }

            override fun removeOnPageChangeListener() {
                onPageChangeCallback?.let { attachable.unregisterOnPageChangeCallback(it) }
            }

            override fun addOnPageChangeListener(
                onPageChangeListenerHelper: OnPageChangeListenerHelper
            ) {
                onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int, positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        onPageChangeListenerHelper.onPageScrolled(position, positionOffset)
                    }
                }
                attachable.registerOnPageChangeCallback(onPageChangeCallback!!)
            }
        }
    }
}