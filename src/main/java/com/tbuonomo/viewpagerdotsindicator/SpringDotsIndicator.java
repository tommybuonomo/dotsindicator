package com.tbuonomo.viewpagerdotsindicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.tbuonomo.materialsquareloading.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.widget.LinearLayout.HORIZONTAL;

public class SpringDotsIndicator extends FrameLayout {
  private static final int DEFAULT_POINT_COLOR = Color.WHITE;
  public static final float DEFAULT_WIDTH_FACTOR = 2.5f;

  private List<ImageView> strokeDots;
  private View dotIndicator;
  private ViewPager viewPager;
  private int dotStrokeSize;
  private int dotSpacing;
  private int dotStrokeWidth;
  private int dotIndicatorSize;
  private int dotIndicatorAdditionalSize;
  private int dotsColor;
  private int indicatorDotColor;
  private int horizontalMargin;
  private SpringAnimation dotIndicatorSpring;
  private LinearLayout strokeDotsLinearLayout;

  private boolean dotsClickable;
  private ViewPager.OnPageChangeListener pageChangedListener;

  public SpringDotsIndicator(Context context) {
    super(context);
    init(context, null);
  }

  public SpringDotsIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public SpringDotsIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    strokeDots = new ArrayList<>();
    strokeDotsLinearLayout = new LinearLayout(context);
    LayoutParams linearParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    horizontalMargin = dpToPx(24);
    linearParams.setMargins(horizontalMargin, 0, horizontalMargin, 0);
    strokeDotsLinearLayout.setLayoutParams(linearParams);
    strokeDotsLinearLayout.setOrientation(HORIZONTAL);
    addView(strokeDotsLinearLayout);

    dotStrokeSize = dpToPx(16); // 16dp
    dotSpacing = dpToPx(4); // 4dp
    dotStrokeWidth = dpToPx(2); // 2dp
    dotIndicatorAdditionalSize = dpToPx(1); // 1dp additional to fill the stroke dots
    dotsColor = DEFAULT_POINT_COLOR;
    indicatorDotColor = dotsColor;
    dotsClickable = true;

    if (attrs != null) {
      @SuppressLint("CustomViewStyleable") TypedArray dotsAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.DotsIndicator);
      TypedArray springDotsAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.SpringDotsIndicator);

      dotsColor = dotsAttributes.getColor(R.styleable.DotsIndicator_dotsColor, DEFAULT_POINT_COLOR);
      indicatorDotColor = springDotsAttributes.getColor(R.styleable.SpringDotsIndicator_indicatorDotColor, dotsColor);
      setUpCircleColors(dotsColor);

      dotStrokeSize = (int) dotsAttributes.getDimension(R.styleable.DotsIndicator_dotsSize, dotStrokeSize);
      dotSpacing = (int) dotsAttributes.getDimension(R.styleable.DotsIndicator_dotsSpacing, dotSpacing);
      dotStrokeWidth = (int) springDotsAttributes.getDimension(R.styleable.SpringDotsIndicator_dotsStrokeWidth, dotStrokeWidth);

      dotsAttributes.recycle();
      springDotsAttributes.recycle();
    } else {
      setUpCircleColors(DEFAULT_POINT_COLOR);
    }
    dotIndicatorSize = dotStrokeSize - dotStrokeWidth * 2 + dpToPx(1);
  }

  private int dpToPx(int dp) {
    return (int) getContext().getResources().getDisplayMetrics().density * dp;
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    refreshDots();
  }

  private void refreshDots() {
    if (dotIndicator == null) {
      setUpDotIndicator();
    }

    if (viewPager != null && viewPager.getAdapter() != null) {
      // Check if we need to refresh the strokeDots count
      if (strokeDots.size() < viewPager.getAdapter().getCount()) {
        addStrokeDots(viewPager.getAdapter().getCount() - strokeDots.size());
      } else if (strokeDots.size() > viewPager.getAdapter().getCount()) {
        removeDots(strokeDots.size() - viewPager.getAdapter().getCount());
      }
      setUpDotsAnimators();
    } else {
      Log.e(SpringDotsIndicator.class.getSimpleName(), "You have to set an adapter to the view pager before !");
    }
  }

  private void setUpDotIndicator() {
    dotIndicator = buildDot(false);
    addView(dotIndicator);
    dotIndicatorSpring = new SpringAnimation(dotIndicator, SpringAnimation.TRANSLATION_X);
    SpringForce springForce = new SpringForce(0);
    springForce.setDampingRatio(0.25f);
    springForce.setStiffness(300);
    dotIndicatorSpring.setSpring(springForce);
  }

  private void addStrokeDots(int count) {
    for (int i = 0; i < count; i++) {
      ViewGroup dot = buildDot(true);
      final int finalI = i;
      dot.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          if (dotsClickable && viewPager != null && viewPager.getAdapter() != null && finalI < viewPager.getAdapter().getCount()) {
            viewPager.setCurrentItem(finalI, true);
          }
        }
      });

      strokeDots.add((ImageView) dot.findViewById(R.id.dot));
      strokeDotsLinearLayout.addView(dot);
    }
  }

  private ViewGroup buildDot(boolean stroke) {
    ViewGroup dot = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.dot_layout, this, false);
    ImageView dotView = dot.findViewById(R.id.dot);
    dotView.setBackground(ContextCompat.getDrawable(getContext(), stroke ? R.drawable.dot_stroke_background : R.drawable.dot_background));
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dotView.getLayoutParams();
    params.width = params.height = stroke ? dotStrokeSize : dotIndicatorSize;
    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

    params.setMargins(dotSpacing, 0, dotSpacing, 0);

    ((GradientDrawable) dotView.getBackground()).setCornerRadius(dpToPx(4));
    if (stroke) {
      ((GradientDrawable) dotView.getBackground()).setStroke(dotStrokeWidth, dotsColor);
    } else {
      ((GradientDrawable) dotView.getBackground()).setColor(indicatorDotColor);
    }
    return dot;
  }

  private void removeDots(int count) {
    for (int i = 0; i < count; i++) {
      strokeDotsLinearLayout.removeViewAt(strokeDotsLinearLayout.getChildCount() - 1);
      strokeDots.remove(strokeDots.size() - 1);
    }
  }

  private void setUpDotsAnimators() {
    if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 0) {
      if (pageChangedListener != null) {
        viewPager.removeOnPageChangeListener(pageChangedListener);
      }
      setUpOnPageChangedListener();
      viewPager.addOnPageChangeListener(pageChangedListener);
    }
  }

  private void setUpOnPageChangedListener() {
    pageChangedListener = new ViewPager.OnPageChangeListener() {

      @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        float globalPositionOffsetPixels = position * (dotStrokeSize + dotSpacing * 2) + (dotStrokeSize + dotSpacing * 2) * positionOffset;
        float indicatorTranslationX = globalPositionOffsetPixels + horizontalMargin + dotStrokeWidth - dotIndicatorAdditionalSize / 2;
        dotIndicatorSpring.getSpring().setFinalPosition(indicatorTranslationX);

        if (!dotIndicatorSpring.isRunning()) {
          dotIndicatorSpring.start();
        }
      }

      @Override public void onPageSelected(int position) {
      }

      @Override public void onPageScrollStateChanged(int state) {
      }
    };
  }

  private void setUpCircleColors(int color) {
    if (strokeDots != null) {
      for (ImageView dot : strokeDots) {
        ((GradientDrawable) dot.getBackground()).setColor(color);
      }
    }
  }

  private void setUpViewPager() {
    if (viewPager.getAdapter() != null) {
      viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
        @Override public void onChanged() {
          super.onChanged();
          refreshDots();
        }
      });
    }
  }

  //*********************************************************
  // Users Methods
  //*********************************************************

  public void setPointsColor(int color) {
    setUpCircleColors(color);
  }

  public void setDotsClickable(boolean dotsClickable) {
    this.dotsClickable = dotsClickable;
  }

  public void setViewPager(ViewPager viewPager) {
    this.viewPager = viewPager;
    setUpViewPager();
    refreshDots();
  }
}
