package com.tbuonomo.viewpagerdotsindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.tbuonomo.materialsquareloading.R;
import java.util.ArrayList;
import java.util.List;

public class DotsIndicator extends LinearLayout {
  private static final int DEFAULT_POINT_COLOR = Color.WHITE;
  public static final float DEFAULT_WIDTH_FACTOR = 2.5f;

  private List<ImageView> dots;
  private ViewPager viewPager;
  private float dotSize;
  private float dotSpacing;
  private int currentPage;
  private float dotsWidthFactor;
  private int dotsColor;

  private boolean dotsClickable;

  public DotsIndicator(Context context) {
    super(context);
    init(context, null);
  }

  public DotsIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public DotsIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    removeViewsIfNeeded();
    setOrientation(HORIZONTAL);

    dotSize = context.getResources().getDisplayMetrics().density * 8; // 8dp
    dotSpacing = context.getResources().getDisplayMetrics().density * 4; // 4dp
    dotsWidthFactor = DEFAULT_WIDTH_FACTOR;
    dotsColor = DEFAULT_POINT_COLOR;
    dotsClickable = true;

    if (attrs != null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DotsIndicator);

      dotsColor = a.getColor(R.styleable.DotsIndicator_dotsColor, DEFAULT_POINT_COLOR);
      setUpCircleColors(dotsColor);

      dotsWidthFactor = a.getFloat(R.styleable.DotsIndicator_dotsWidthFactor, 2.5f);
      if (dotsWidthFactor < 1) {
        dotsWidthFactor = 2.5f;
      }

      dotSize = a.getDimension(R.styleable.DotsIndicator_dotsSize, dotSize);
      dotSpacing = a.getDimension(R.styleable.DotsIndicator_dotsSpacing, dotSpacing);

      a.recycle();
    } else {
      setUpCircleColors(DEFAULT_POINT_COLOR);
    }
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
  }

  private void setUpDots() {
    removeViewsIfNeeded();
    if (viewPager != null && viewPager.getAdapter() != null) {
      dots = new ArrayList<>();

      for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
        View dot = LayoutInflater.from(getContext()).inflate(R.layout.dot_layout, this, false);
        ImageView imageView = dot.findViewById(R.id.dot);
        RelativeLayout.LayoutParams params =
            (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = params.height = (int) dotSize;
        params.setMargins((int) dotSpacing, 0, (int) dotSpacing, 0);
        ((GradientDrawable) imageView.getBackground()).setCornerRadius(dotSize / 2);
        ((GradientDrawable) imageView.getBackground()).setColor(dotsColor);

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

        dots.add(imageView);
        addView(dot);
      }
    }
  }

  private void setUpDotsAnimators() {
    if (viewPager != null
        && viewPager.getAdapter() != null
        && viewPager.getAdapter().getCount() > 0) {
      currentPage = viewPager.getCurrentItem();
      View dot = dots.get(currentPage);

      if (dot != null) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dot.getLayoutParams();
        params.width = (int) (dotSize * dotsWidthFactor);
        dot.setLayoutParams(params);
      }

      viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        private int lastPage;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
          if (position != currentPage && positionOffset == 0 || currentPage < position) {
            setDotWidth(dots.get(currentPage), (int) dotSize);
            currentPage = position;
          }

          if (Math.abs(currentPage - position) > 1) {
            setDotWidth(dots.get(currentPage), (int) dotSize);
            currentPage = lastPage;
          }

          ImageView dot = dots.get(currentPage);

          ImageView nextDot = null;
          if (currentPage == position && currentPage + 1 < dots.size()) {
            nextDot = dots.get(currentPage + 1);
          } else if (currentPage > position) {
            nextDot = dot;
            dot = dots.get(currentPage - 1);
          }

          int dotWidth = (int) (dotSize + (dotSize * (dotsWidthFactor - 1) * (1 - positionOffset)));
          setDotWidth(dot, dotWidth);

          if (nextDot != null) {
            int nextDotWidth =
                (int) (dotSize + (dotSize * (dotsWidthFactor - 1) * (positionOffset)));
            setDotWidth(nextDot, nextDotWidth);
          }

          lastPage = position;
        }

        private void setDotWidth(ImageView dot, int dotWidth) {
          ViewGroup.LayoutParams dotParams = dot.getLayoutParams();
          dotParams.width = dotWidth;
          dot.setLayoutParams(dotParams);
        }

        @Override public void onPageSelected(int position) {
        }

        @Override public void onPageScrollStateChanged(int state) {
        }
      });
    }
  }

  private void setUpCircleColors(int color) {
    if (dots != null) {
      for (ImageView elevationItem : dots) {
        ((GradientDrawable) elevationItem.getBackground()).setColor(color);
      }
    }
  }

  private void setUpViewPager() {
    if (viewPager.getAdapter() != null) {
      viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
        @Override public void onChanged() {
          super.onChanged();
          setUpDots();
          setUpDotsAnimators();
          setUpViewPager();
        }
      });
    }
  }

  private void removeViewsIfNeeded() {
    if (getChildCount() > 0) {
      removeAllViews();
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
    setUpDots();
    setUpDotsAnimators();
    setUpViewPager();
  }
}
