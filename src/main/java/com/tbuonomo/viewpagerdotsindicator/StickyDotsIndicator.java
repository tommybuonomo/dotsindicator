package com.tbuonomo.viewpagerdotsindicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.animation.FloatPropertyCompat;
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

public class StickyDotsIndicator extends FrameLayout {
  private static final int DEFAULT_POINT_COLOR = Color.CYAN;

  private List<ImageView> strokeDots;
  private View dotIndicator;
  private ViewPager viewPager;

  // Attributes
  private int dotsSize;
  private int dotsSpacing;
  private int dotsStrokeWidth;
  private int dotsCornerRadius;
  private int dotsColor;

  private int dotIndicatorAdditionalSize;
  private int horizontalMargin;
  private SpringAnimation dotIndicatorXSpring;
  private SpringAnimation dotIndicatorWidthSpring;
  private LinearLayout strokeDotsLinearLayout;

  private boolean dotsClickable;
  private ViewPager.OnPageChangeListener pageChangedListener;

  public StickyDotsIndicator(Context context) {
    super(context);
    init(context, null);
  }

  public StickyDotsIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public StickyDotsIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
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

    dotsSize = dpToPx(16); // 16dp
    dotsSpacing = dpToPx(4); // 4dp
    dotsStrokeWidth = dpToPx(2); // 2dp
    dotIndicatorAdditionalSize = dpToPx(1); // 1dp additional to fill the stroke dots
    dotsCornerRadius = dotsSize / 2; // 1dp additional to fill the stroke dots
    dotsColor = DEFAULT_POINT_COLOR;
    dotsClickable = true;

    if (attrs != null) {
      @SuppressLint("CustomViewStyleable") TypedArray dotsAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.DotsIndicator);
      TypedArray springDotsAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.SpringDotsIndicator);

      // Dots attributes
      dotsColor = dotsAttributes.getColor(R.styleable.DotsIndicator_dotsColor, DEFAULT_POINT_COLOR);
      setUpCircleColors(dotsColor);
      dotsSize = (int) dotsAttributes.getDimension(R.styleable.DotsIndicator_dotsSize, dotsSize);
      dotsSpacing = (int) dotsAttributes.getDimension(R.styleable.DotsIndicator_dotsSpacing, dotsSpacing);
      dotsCornerRadius = (int) dotsAttributes.getDimension(R.styleable.DotsIndicator_dotsCornerRadius, dotsCornerRadius);

      // Spring dots attributes
      dotsStrokeWidth = (int) springDotsAttributes.getDimension(R.styleable.SpringDotsIndicator_dotsStrokeWidth, dotsStrokeWidth);

      dotsAttributes.recycle();
      springDotsAttributes.recycle();
    } else {
      setUpCircleColors(DEFAULT_POINT_COLOR);
    }

    if (isInEditMode()) {
      addStrokeDots(5);
      addView(buildDot(false));
    }
  }

  private int dpToPx(int dp) {
    return (int) getContext().getResources().getDisplayMetrics().density * dp;
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
      Log.e(StickyDotsIndicator.class.getSimpleName(), "You have to set an adapter to the view pager before !");
    }
  }

  private void setUpDotIndicator() {
    dotIndicator = buildDot(false);
    addView(dotIndicator);
    dotIndicatorXSpring = new SpringAnimation(dotIndicator, SpringAnimation.TRANSLATION_X);
    SpringForce springForceX = new SpringForce(0);
    springForceX.setDampingRatio(1f);
    springForceX.setStiffness(800);
    dotIndicatorXSpring.setSpring(springForceX);

    FloatPropertyCompat floatPropertyCompat = new FloatPropertyCompat("DotsWidth") {
      @Override public float getValue(Object object) {
        return ((View) object).findViewById(R.id.dot).getLayoutParams().width;
      }

      @Override public void setValue(Object object, float value) {
        View view = ((View) object).findViewById(R.id.dot);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) value;
        view.requestLayout();
      }
    };
    dotIndicatorWidthSpring = new SpringAnimation(dotIndicator, floatPropertyCompat);
    dotIndicatorWidthSpring.setMinimumVisibleChange(SpringAnimation.MIN_VISIBLE_CHANGE_PIXELS);
    SpringForce springForceWidth = new SpringForce(0);
    springForceWidth.setDampingRatio(1f);
    springForceWidth.setStiffness(800);
    dotIndicatorWidthSpring.setSpring(springForceWidth);
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
    dotView.setBackground(ContextCompat.getDrawable(getContext(), stroke ? R.drawable.dot_stroke_background : R.drawable.spring_dot_background));
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dotView.getLayoutParams();
    params.width = params.height = dotsSize;
    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

    params.setMargins(dotsSpacing, 0, dotsSpacing, 0);

    GradientDrawable dotBackground = (GradientDrawable) dotView.getBackground();
    if (stroke) {
      dotBackground.setStroke(dotsStrokeWidth, dotsColor);
    } else {
      dotBackground.setColor(dotsColor);
    }
    dotBackground.setCornerRadius(dotsCornerRadius);
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
        int stepX = dotsSize + dotsSpacing * 2;
        int paddingStart = horizontalMargin - dotIndicatorAdditionalSize / 2;

        if (positionOffset >= 0 && positionOffset < 0.1f) {
          dotIndicatorXSpring.getSpring().setFinalPosition(paddingStart + position * stepX);
          dotIndicatorWidthSpring.getSpring().setFinalPosition(dotsSize);
        } else if (positionOffset >= 0.1f && positionOffset < 0.9f) {
          dotIndicatorXSpring.getSpring().setFinalPosition(paddingStart + position * stepX);
          dotIndicatorWidthSpring.getSpring().setFinalPosition(dotsSize + stepX);
        } else {
          dotIndicatorXSpring.getSpring().setFinalPosition(paddingStart + (position + 1) * stepX);
          dotIndicatorWidthSpring.getSpring().setFinalPosition(dotsSize);
        }

        if (!dotIndicatorXSpring.isRunning()) {
          dotIndicatorXSpring.start();
        }

        if (!dotIndicatorWidthSpring.isRunning()) {
          dotIndicatorWidthSpring.start();
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
