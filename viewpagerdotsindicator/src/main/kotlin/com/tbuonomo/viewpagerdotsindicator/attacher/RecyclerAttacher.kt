package com.tbuonomo.viewpagerdotsindicator.attacher

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper
import kotlin.math.max

internal class RecyclerAttacher(val snapHelper: PagerSnapHelper, val initialDx: Int) :
    DotsIndicatorAttacher<RecyclerView, RecyclerView.Adapter<*>>() {
    override fun getAdapterFromAttachable(attachable: RecyclerView): RecyclerView.Adapter<RecyclerView.ViewHolder>? =
        attachable.adapter

    override fun registerAdapterDataChangedObserver(
        attachable: RecyclerView,
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
        attachable: RecyclerView,
        adapter: RecyclerView.Adapter<*>
    ): BaseDotsIndicator.Pager {
        return object : BaseDotsIndicator.Pager {
            private var onScrollListener: RecyclerView.OnScrollListener? = null
            private var currentPosition: Int? = null
            private var currentDx = initialDx

            override val isNotEmpty: Boolean
                get() = adapter.itemCount > 0

            override val currentItem: Int
                get() = currentPosition ?: attachable.layoutManager?.let { snapHelper.findSnapView(it) }?.let {
                    (attachable.layoutManager as LinearLayoutManager).getPosition(it)
                } ?: 0

            override val isEmpty: Boolean
                get() = adapter.itemCount == 0

            override val count: Int
                get() = adapter.itemCount

            override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
                if (smoothScroll) {
                    attachable.smoothScrollToPosition(item)
                } else {
                    attachable.scrollToPosition(item)
                }
            }

            override fun removeOnPageChangeListener() {
                onScrollListener?.let(attachable::removeOnScrollListener)
            }

            override fun addOnPageChangeListener(
                    onPageChangeListenerHelper: OnPageChangeListenerHelper
            ) {
                onScrollListener = object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(attachable: RecyclerView, dx: Int, dy: Int) {
                        currentDx += dx

                        val view = snapHelper.findSnapView(attachable.layoutManager)
                        if (view != null) {
                            val width = view.width - view.paddingLeft - view.paddingRight

                            if (currentPosition == null && initialDx > width) {
                                setCurrentItem(max(currentDx / width, count), false) // reset
                            }

                            val newPosition = currentDx / width
                            currentPosition = newPosition

                            onPageChangeListenerHelper.onPageScrolled(
                                    newPosition,
                                    try {
                                        (currentDx % width).toFloat() / width
                                    } catch (e: ArithmeticException) {
                                        0f
                                    }
                            )
                        }
                    }
                }
                attachable.addOnScrollListener(onScrollListener!!)
            }
        }
    }
}