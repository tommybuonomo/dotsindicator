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
import com.tbuonomo.viewpagerdotsindicator.R;
import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.HORIZONTAL;

public class SpringDotsIndicator extends FrameLayout {
  private static final int DEFAULT_POINT_COLOR = Color.CYAN;
  public static final float DEFAULT_DAMPING_RATIO = 0.5f;
  public static final int DEFAULT_STIFFNESS = 300;

  private List<ImageView> strokeDots;
  private View dotIndicator;
  private ViewPager viewPager;

  // Attributes
  private int dotsStrokeSize;
  private int dotsSpacing;
  private int dotsStrokeWidth;
  private int dotsCornerRadius;
  private int dotsColor;
  private float stiffness;
  private float dampingRatio;

  private int dotIndicatorSize;
  private int dotIndicatorAdditionalSize;
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
    LayoutParams linearParams =
        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    horizontalMargin = dpToPx(24);
    linearParams.setMargins(horizontalMargin, 0, horizontalMargin, 0);
    strokeDotsLinearLayout.setLayoutParams(linearParams);
    strokeDotsLinearLayout.setOrientation(HORIZONTAL);
    addView(strokeDotsLinearLayout);

    dotsStrokeSize = dpToPx(16); // 16dp
    dotsSpacing = dpToPx(4); // 4dp
    dotsStrokeWidth = dpToPx(2); // 2dp
    dotIndicatorAdditionalSize = dpToPx(1); // 1dp additional to fill the stroke dots
    dotsCornerRadius = dotsStrokeSize / 2; // 1dp additional to fill the stroke dots
    dotsColor = DEFAULT_POINT_COLOR;
    stiffness = DEFAULT_STIFFNESS;
    dampingRatio = DEFAULT_DAMPING_RATIO;
    dotsClickable = true;

    if (attrs != null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SpringDotsIndicator);

      // Dots attributes
      dotsColor = a.getColor(R.styleable.SpringDotsIndicator_dotsColor, DEFAULT_POINT_COLOR);
      setUpCircleColors(dotsColor);
      dotsStrokeSize =
          (int) a.getDimension(R.styleable.SpringDotsIndicator_dotsSize, dotsStrokeSize);
      dotsSpacing = (int) a.getDimension(R.styleable.SpringDotsIndicator_dotsSpacing, dotsSpacing);
      dotsCornerRadius = (int) a.getDimension(R.styleable.SpringDotsIndicator_dotsCornerRadius,
          dotsStrokeSize / 2);
      stiffness = a.getFloat(R.styleable.SpringDotsIndicator_stiffness, stiffness);
      dampingRatio = a.getFloat(R.styleable.SpringDotsIndicator_dampingRatio, dampingRatio);

      // Spring dots attributes
      dotsStrokeWidth =
          (int) a.getDimension(R.styleable.SpringDotsIndicator_dotsStrokeWidth, dotsStrokeWidth);

      a.recycle();
    } else {
      setUpCircleColors(DEFAULT_POINT_COLOR);
    }
    dotIndicatorSize = dotsStrokeSize - dotsStrokeWidth * 2 + dotIndicatorAdditionalSize;

    if (isInEditMode()) {
      addStrokeDots(5);
      addView(buildDot(false));
    }
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
      Log.e(SpringDotsIndicator.class.getSimpleName(),
          "You have to set an adapter to the view pager before !");
    }
  }

  private void setUpDotIndicator() {
    dotIndicator = buildDot(false);
    addView(dotIndicator);
    dotIndicatorSpring = new SpringAnimation(dotIndicator, SpringAnimation.TRANSLATION_X);
    SpringForce springForce = new SpringForce(0);
    springForce.setDampingRatio(dampingRatio);
    springForce.setStiffness(stiffness);
    dotIndicatorSpring.setSpring(springForce);
  }

  private void addStrokeDots(int count) {
    for (int i = 0; i < count; i++) {
      ViewGroup dot = buildDot(true);
      final int finalI = i;
      dot.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          if (dotsClickable
              && viewPager != null
              && viewPager.getAdapter() != null
              && finalI < viewPager.getAdapter().getCount()) {
            viewPager.setCurrentItem(finalI, true);
          }
        }
      });

      strokeDots.add((ImageView) dot.findViewById(R.id.dot));
      strokeDotsLinearLayout.addView(dot);
    }
  }

  private ViewGroup buildDot(boolean stroke) {
    ViewGroup dot = (ViewGroup) LayoutInflater.from(getContext())
        .inflate(R.layout.spring_dot_layout, this, false);
    ImageView dotView = dot.findViewById(R.id.spring_dot);
    dotView.setBackground(ContextCompat.getDrawable(getContext(),
        stroke ? R.drawable.spring_dot_stroke_background : R.drawable.spring_dot_background));
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dotView.getLayoutParams();
    params.width = params.height = stroke ? dotsStrokeSize : dotIndicatorSize;
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
    if (viewPager != null
        && viewPager.getAdapter() != null
        && viewPager.getAdapter().getCount() > 0) {
      if (pageChangedListener != null) {
        viewPager.removeOnPageChangeListener(pageChangedListener);
      }
      setUpOnPageChangedListener();
      viewPager.addOnPageChangeListener(pageChangedListener);
    }
  }

  private void setUpOnPageChangedListener() {
    pageChangedListener = new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        float globalPositionOffsetPixels = position * (dotsStrokeSize + dotsSpacing * 2)
            + (dotsStrokeSize + dotsSpacing * 2) * positionOffset;
        float indicatorTranslationX =
            globalPositionOffsetPixels + horizontalMargin + dotsStrokeWidth
                - dotIndicatorAdditionalSize / 2;
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

  private int dpToPx(int dp) {
    return (int) (getContext().getResources().getDisplayMetrics().density * dp);
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
