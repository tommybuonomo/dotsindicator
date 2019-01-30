package com.tbuonomo.viewpagerdotsindicator;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.List;

public class DotsIndicator extends LinearLayout {
  private static final int DEFAULT_POINT_COLOR = Color.CYAN;
  public static final float DEFAULT_WIDTH_FACTOR = 2.5f;

  private List<ImageView> dots;
  private ViewPager viewPager;
  private float dotsSize;
  private float dotsCornerRadius;
  private float dotsSpacing;
  private float dotsWidthFactor;
  private int dotsColor;
  private int selectedDotColor;
  private boolean progressMode;

  private boolean dotsClickable;

  private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
  private OnPageChangeListenerHelper onPageChangeListenerHelper;

  public DotsIndicator(Context context) {
    this(context, null);
  }

  public DotsIndicator(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DotsIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    dots = new ArrayList<>();
    setOrientation(HORIZONTAL);

    dotsSize = dpToPx(16); // 16dp
    dotsSpacing = dpToPx(4); // 4dp
    dotsCornerRadius = dotsSize / 2;

    dotsWidthFactor = DEFAULT_WIDTH_FACTOR;
    dotsColor = DEFAULT_POINT_COLOR;
    dotsClickable = true;

    if (attrs != null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DotsIndicator);

      dotsColor = a.getColor(R.styleable.DotsIndicator_dotsColor, DEFAULT_POINT_COLOR);
      selectedDotColor = a.getColor(R.styleable.DotsIndicator_selectedDotColor, DEFAULT_POINT_COLOR);

      dotsWidthFactor = a.getFloat(R.styleable.DotsIndicator_dotsWidthFactor, 2.5f);
      if (dotsWidthFactor < 1) {
        dotsWidthFactor = 2.5f;
      }

      dotsSize = a.getDimension(R.styleable.DotsIndicator_dotsSize, dotsSize);
      dotsCornerRadius = (int) a.getDimension(R.styleable.DotsIndicator_dotsCornerRadius, dotsSize / 2);
      dotsSpacing = a.getDimension(R.styleable.DotsIndicator_dotsSpacing, dotsSpacing);

      progressMode = a.getBoolean(R.styleable.DotsIndicator_progressMode, false);

      a.recycle();
    }

    if (isInEditMode()) {
      addDots(5);
    }

    refreshDots();
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    refreshDots();
  }

  private void refreshDots() {
    if (viewPager != null && viewPager.getAdapter() != null) {
      post(new Runnable() {
        @Override public void run() {
          // Check if we need to refresh the dots count
          refreshDotsCount();
          refreshDotsColors();
          refreshDotsSize();
          refreshOnPageChangedListener();
        }
      });
    } else {
      Log.e(DotsIndicator.class.getSimpleName(), "You have to set an adapter to the view pager before !");
    }
  }

  private void refreshDotsCount() {
    if (dots.size() < viewPager.getAdapter().getCount()) {
      addDots(viewPager.getAdapter().getCount() - dots.size());
    } else if (dots.size() > viewPager.getAdapter().getCount()) {
      removeDots(dots.size() - viewPager.getAdapter().getCount());
    }
  }

  private void addDots(int count) {
    for (int i = 0; i < count; i++) {
      View dot = LayoutInflater.from(getContext()).inflate(R.layout.dot_layout, this, false);
      ImageView imageView = dot.findViewById(R.id.dot);
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
      params.width = params.height = (int) dotsSize;
      params.setMargins((int) dotsSpacing, 0, (int) dotsSpacing, 0);
      DotsGradientDrawable background = new DotsGradientDrawable();
      background.setCornerRadius(dotsCornerRadius);
      if (isInEditMode()) {
        background.setColor(0 == i ? selectedDotColor : dotsColor);
      } else {
        background.setColor(viewPager.getCurrentItem() == i ? selectedDotColor : dotsColor);
      }
      imageView.setBackground(background);

      final int finalI = i;
      dot.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          if (dotsClickable && viewPager != null && viewPager.getAdapter() != null && finalI < viewPager.getAdapter()
              .getCount()) {
            viewPager.setCurrentItem(finalI, true);
          }
        }
      });

      dots.add(imageView);
      addView(dot);
    }
  }

  private void removeDots(int count) {
    for (int i = 0; i < count; i++) {
      removeViewAt(getChildCount() - 1);
      dots.remove(dots.size() - 1);
    }
  }

  private void refreshOnPageChangedListener() {
    if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 0) {
      viewPager.removeOnPageChangeListener(onPageChangeListenerHelper);
      onPageChangeListenerHelper = buildOnPageChangedListener();
      viewPager.addOnPageChangeListener(onPageChangeListenerHelper);
      onPageChangeListenerHelper.onPageScrolled(viewPager.getCurrentItem(), -1, 0f);
    }
  }

  private OnPageChangeListenerHelper buildOnPageChangedListener() {
    return new OnPageChangeListenerHelper() {
      @Override void onPageScrolled(int selectedPosition, int nextPosition, float positionOffset) {
        if (selectedPosition == -1) {
          return;
        }

        ImageView selectedDot = dots.get(selectedPosition);

        // Selected dot
        int selectedDotWidth = (int) (dotsSize + (dotsSize * (dotsWidthFactor - 1) * (1 - positionOffset)) + 0.5);
        setDotWidth(selectedDot, selectedDotWidth);

        // Next dot
        if (nextPosition == -1) {
          return;
        }

        ImageView nextDot = dots.get(nextPosition);
        if (nextDot != null) {
          int nextDotWidth = (int) (dotsSize + (dotsSize * (dotsWidthFactor - 1) * (positionOffset)) + 0.5);
          setDotWidth(nextDot, nextDotWidth);

          DotsGradientDrawable selectedDotBackground = (DotsGradientDrawable) selectedDot.getBackground();
          DotsGradientDrawable nextDotBackground = (DotsGradientDrawable) nextDot.getBackground();

          if (selectedDotColor != dotsColor) {
            int selectedColor = (int) argbEvaluator.evaluate(positionOffset, selectedDotColor, dotsColor);
            int nextColor = (int) argbEvaluator.evaluate(positionOffset, dotsColor, selectedDotColor);

            nextDotBackground.setColor(nextColor);

            if (progressMode && selectedPosition <= viewPager.getCurrentItem()) {
              selectedDotBackground.setColor(selectedDotColor);
            } else {
              selectedDotBackground.setColor(selectedColor);
            }
          }
        }

        invalidate();
      }

      @Override void resetPosition(int position) {
        setDotWidth(dots.get(position), (int) dotsSize);
      }

      @Override int getPageCount() {
        return dots.size();
      }
    };
  }

  private void setDotWidth(ImageView dot, int dotWidth) {
    ViewGroup.LayoutParams dotParams = dot.getLayoutParams();
    dotParams.width = dotWidth;
    dot.setLayoutParams(dotParams);
  }

  private void refreshDotsColors() {
    if (dots == null) {
      return;
    }
    for (int i = 0; i < dots.size(); i++) {
      ImageView elevationItem = dots.get(i);
      DotsGradientDrawable background = (DotsGradientDrawable) elevationItem.getBackground();

      if (i == viewPager.getCurrentItem() || (progressMode && i < viewPager.getCurrentItem())) {
        background.setColor(selectedDotColor);
      } else {
        background.setColor(dotsColor);
      }

      elevationItem.setBackground(background);
      elevationItem.invalidate();
    }
  }

  private void refreshDotsSize() {
    if (dots == null) {
      return;
    }
    for (int i = 0; i < viewPager.getCurrentItem(); i++) {
      setDotWidth(dots.get(i), (int) dotsSize);
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

  private int dpToPx(int dp) {
    return (int) (getContext().getResources().getDisplayMetrics().density * dp + 0.5);
  }

  //*********************************************************
  // Users Methods
  //*********************************************************

  public void setPointsColor(int color) {
    dotsColor = color;
    refreshDotsColors();
  }

  public void setSelectedPointColor(int color) {
    selectedDotColor = color;
    refreshDotsColors();
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
