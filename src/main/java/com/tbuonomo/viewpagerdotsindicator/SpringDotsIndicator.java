package com.tbuonomo.viewpagerdotsindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import java.util.List;

import static android.widget.LinearLayout.HORIZONTAL;

public class SpringDotsIndicator extends FrameLayout {
  private static final int DEFAULT_POINT_COLOR = Color.WHITE;
  public static final float DEFAULT_WIDTH_FACTOR = 2.5f;

  private List<ImageView> strokeDots;
  private View dotIndicator;
  private ViewPager viewPager;
  private float dotSize;
  private float dotSpacing;
  private int currentPage;
  private int dotsColor;
  private SpringAnimation springAnimation;
  private SpringForce springForce;
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
    strokeDotsLinearLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    strokeDotsLinearLayout.setOrientation(HORIZONTAL);
    addView(strokeDotsLinearLayout);

    dotSize = dpToPx(8); // 8dp
    dotSpacing = dpToPx(4); // 4dp
    dotsColor = DEFAULT_POINT_COLOR;
    dotsClickable = true;

    if (attrs != null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DotsIndicator);

      dotsColor = a.getColor(R.styleable.DotsIndicator_dotsColor, DEFAULT_POINT_COLOR);
      setUpCircleColors(dotsColor);

      dotSize = a.getDimension(R.styleable.DotsIndicator_dotsSize, dotSize);
      dotSpacing = a.getDimension(R.styleable.DotsIndicator_dotsSpacing, dotSpacing);

      a.recycle();
    } else {
      setUpCircleColors(DEFAULT_POINT_COLOR);
    }
  }

  private float dpToPx(int dp) {
    return getContext().getResources().getDisplayMetrics().density * dp;
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
    springAnimation = new SpringAnimation(dotIndicator, SpringAnimation.TRANSLATION_X);
    springForce = new SpringForce(0);
    springForce.setDampingRatio(0.3f);
    springForce.setStiffness(300);
    springAnimation.setSpring(springForce);
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
    ImageView imageView = dot.findViewById(R.id.dot);
    imageView.setBackground(ContextCompat.getDrawable(getContext(), stroke ? R.drawable.dot_stroke_background : R.drawable.dot_background));
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
    params.width = params.height = (int) dotSize;
    params.setMargins((int) dotSpacing, 0, (int) dotSpacing, 0);
    ((GradientDrawable) imageView.getBackground()).setCornerRadius(dotSize / 2);
    if (stroke) {
      ((GradientDrawable) imageView.getBackground()).setStroke((int) dpToPx(2), dotsColor);
    } else {
      ((GradientDrawable) imageView.getBackground()).setColor(dotsColor);
    }
    return dot;
  }

  private void removeDots(int count) {
    for (int i = 0; i < count; i++) {
      removeViewAt(getChildCount() - 1);
      strokeDots.remove(strokeDots.size() - 1);
    }
  }

  private void setUpDotsAnimators() {
    if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 0) {
      if (currentPage < strokeDots.size()) {
        View dot = strokeDots.get(currentPage);

        if (dot != null) {
          ViewGroup.LayoutParams params = dot.getLayoutParams();
          params.width = (int) dotSize;
          dot.setLayoutParams(params);
        }
      }

      currentPage = viewPager.getCurrentItem();
      if (currentPage >= strokeDots.size()) {
        currentPage = strokeDots.size() - 1;
        viewPager.setCurrentItem(currentPage, false);
      }

      //todo current dot
      View dot = strokeDots.get(currentPage);

      if (dot != null) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dot.getLayoutParams();
        params.width = (int) (dotSize);
        dot.setLayoutParams(params);
      }
      if (pageChangedListener != null) {
        viewPager.removeOnPageChangeListener(pageChangedListener);
      }
      setUpOnPageChangedListener();
      viewPager.addOnPageChangeListener(pageChangedListener);
    }
  }

  private void setUpOnPageChangedListener() {
    pageChangedListener = new ViewPager.OnPageChangeListener() {
      private int lastPage;

      @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //if (position != currentPage && positionOffset == 0 || currentPage < position) {
        //  setDotWidth(strokeDots.get(currentPage), (int) dotSize);
        //  currentPage = position;
        //}
        //
        //if (Math.abs(currentPage - position) > 1) {
        //  setDotWidth(strokeDots.get(currentPage), (int) dotSize);
        //  currentPage = lastPage;
        //}
        //
        //ImageView dot = strokeDots.get(currentPage);
        //
        //ImageView nextDot = null;
        //if (currentPage == position && currentPage + 1 < strokeDots.size()) {
        //  nextDot = strokeDots.get(currentPage + 1);
        //} else if (currentPage > position) {
        //  nextDot = dot;
        //  dot = strokeDots.get(currentPage - 1);
        //}
        //
        //int dotWidth = (int) (dotSize + (dotSize * (dotsWidthFactor - 1) * (1 - positionOffset)));
        //setDotWidth(dot, dotWidth);
        //
        //if (nextDot != null) {
        //  int nextDotWidth = (int) (dotSize + (dotSize * (dotsWidthFactor - 1) * (positionOffset)));
        //  setDotWidth(nextDot, nextDotWidth);
        //}
        //
        //lastPage = position;

        float offset = position * (dotSize + dotSpacing * 2) + (dotSize + dotSpacing * 2) * positionOffset;
        Log.i(SpringDotsIndicator.class.getSimpleName(), "onPageScrolled: " + offset);
        springForce.setFinalPosition(offset);

        if (!springAnimation.isRunning()) {
          springAnimation.start();
        }

        Log.i(SpringDotsIndicator.class.getSimpleName(), "onPageScrolled: " + position + " " + positionOffset + " " + positionOffsetPixels);
      }

      private void setDotWidth(ImageView dot, int dotWidth) {
        RelativeLayout.LayoutParams dotParams = (RelativeLayout.LayoutParams) dot.getLayoutParams();
        dotParams.width = dotWidth;
        dot.setLayoutParams(dotParams);
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
