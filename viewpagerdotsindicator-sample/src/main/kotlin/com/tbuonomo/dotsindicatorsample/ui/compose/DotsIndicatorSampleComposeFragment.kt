@file:OptIn(ExperimentalFoundationApi::class)

package com.tbuonomo.dotsindicatorsample.ui.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.tbuonomo.dotsindicatorsample.ui.compose.component.PagePlaceholderItem

class DotsIndicatorSampleComposeFragment : Fragment() {
    companion object {
        private val BackgroundDark = Color(0xFF0069DE)
        private val BackgroundLight = Color(0xFF3EA9FF)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent { DotsIndicatorSampleScreen() }
        }
    }

    @Preview
    @Composable
    fun DotsIndicatorSampleScreen() {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                BackgroundLight,
                                BackgroundDark
                            )
                        )
                    )
            ) {
                HorizontalPager(
                    modifier = Modifier.padding(top = 32.dp),
                    pageCount = 10,
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    pageSpacing = 16.dp
                ) {
                    PagePlaceholderItem()
                }
            }
        }
    }
}