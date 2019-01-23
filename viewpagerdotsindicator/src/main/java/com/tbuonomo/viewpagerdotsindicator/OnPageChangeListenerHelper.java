package com.tbuonomo.viewpagerdotsindicator;

import android.support.v4.view.ViewPager;

public abstract class OnPageChangeListenerHelper implements ViewPager.OnPageChangeListener {
  private int currentPage;
  private int lastPage;

  @Override public final void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    int selectedPosition = currentPage;
    if (position != currentPage && positionOffset == 0 || currentPage < position) {
      resetPosition(selectedPosition);
      selectedPosition = currentPage = position;
    }

    if (Math.abs(currentPage - position) > 1) {
      resetPosition(selectedPosition);
      currentPage = lastPage;
    }

    int nextPosition = -1;
    if (currentPage == position && currentPage + 1 < getPageCount()) {
      nextPosition = currentPage + 1;
    } else if (currentPage > position) {
      nextPosition = selectedPosition;
      selectedPosition = currentPage - 1;
    }

    onPageScrolled(selectedPosition, nextPosition, positionOffset);

    lastPage = position;
  }

  @Override public final void onPageSelected(int position) {
    currentPage = position;
  }

  @Override public final void onPageScrollStateChanged(int i) {

  }

  abstract void onPageScrolled(int selectedPosition, int nextPosition, float positionOffset);

  abstract void resetPosition(int position);

  abstract int getPageCount();
}
