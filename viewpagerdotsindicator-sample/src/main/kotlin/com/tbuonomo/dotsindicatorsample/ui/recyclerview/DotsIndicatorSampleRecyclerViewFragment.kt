package com.tbuonomo.dotsindicatorsample.ui.recyclerview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.tbuonomo.dotsindicatorsample.R
import com.tbuonomo.dotsindicatorsample.core.platform.binding.viewBinding
import com.tbuonomo.dotsindicatorsample.databinding.FragmentRecyclerViewBinding

class DotsIndicatorSampleRecyclerViewFragment : Fragment() {

    companion object {
        private const val debugMode = false
    }

    private val binding by viewBinding(FragmentRecyclerViewBinding::bind)
    private val dotsIndicatorRecyclerViewAdapter = DotIndicatorRecyclerViewAdapter()

    private var recyclerDx: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            recyclerDx = savedInstanceState.getInt("recyclerDx", 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("recyclerDx", recyclerDx)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = dotsIndicatorRecyclerViewAdapter
            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(this@with)

            addOnScrollListener(object: OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    recyclerDx += dx
                }
            })

            binding.dotsIndicator.attachTo(this, pagerSnapHelper, recyclerDx)
            binding.springDotsIndicator.attachTo(this, pagerSnapHelper, recyclerDx)
            binding.wormDotsIndicator.attachTo(this, pagerSnapHelper, recyclerDx)
        }

        binding.addButton.isVisible = debugMode
        binding.removeButton.isVisible = debugMode
        binding.usingSmartNotifyCheckBox.isVisible = debugMode

        if (debugMode) {
            binding.addButton.setOnClickListener {
                dotsIndicatorRecyclerViewAdapter.addItem(binding.usingSmartNotifyCheckBox.isChecked)
            }
            binding.removeButton.setOnClickListener {
                dotsIndicatorRecyclerViewAdapter.removeItem(binding.usingSmartNotifyCheckBox.isChecked)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(
                    R.layout.fragment_recycler_view, container, false
            )
}
