package com.tbuonomo.dotsindicatorsample;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.tbuonomo.dotsindicatorsample.ui.viewpager.DotsIndicatorSampleViewPagerFragment;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DotIndicatorTest {

    @Rule
    public ActivityTestRule<DotsIndicatorSampleViewPagerFragment> activityRule = new ActivityTestRule<>(DotsIndicatorSampleViewPagerFragment.class);

    @Before
    public void setUp() {
    }

    @Test
    public void testSetSpringDotIndicatorColor() {
        // Context of the app under test.
        SpringDotsIndicator springDotsIndicator = activityRule.getActivity().findViewById(R.id.spring_dots_indicator);
        springDotsIndicator.setDotIndicatorColor(Color.BLUE);

        GradientDrawable gradientDrawable = (GradientDrawable) ((ViewGroup) springDotsIndicator.getChildAt(1)).getChildAt(0).getBackground();
        Assert.assertEquals(Color.BLUE, gradientDrawable.getColor().getDefaultColor());
    }
}
