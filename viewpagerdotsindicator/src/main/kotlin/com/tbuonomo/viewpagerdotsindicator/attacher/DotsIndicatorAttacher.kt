package com.tbuonomo.viewpagerdotsindicator.attacher

import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator

internal abstract class DotsIndicatorAttacher<Attachable, Adapter> {
    fun setup(baseDotsIndicator: BaseDotsIndicator, attachable: Attachable) {
        val adapter = getAdapterFromAttachable(attachable)
            ?: throw IllegalStateException(
                "Please set an adapter to the view pager (1 or 2) or the recycler before initializing the dots indicator"
            )

        registerAdapterDataChangedObserver(attachable, adapter) {
            baseDotsIndicator.post { baseDotsIndicator.refreshDots() }
        }

        baseDotsIndicator.pager = buildPager(attachable, adapter)
        baseDotsIndicator.refreshDots()
    }

    abstract fun getAdapterFromAttachable(attachable: Attachable): Adapter?

    abstract fun registerAdapterDataChangedObserver(
        attachable: Attachable,
        adapter: Adapter,
        onChanged: () -> Unit
    )

    abstract fun buildPager(
        attachable: Attachable,
        adapter: Adapter,
    ): BaseDotsIndicator.Pager
}

