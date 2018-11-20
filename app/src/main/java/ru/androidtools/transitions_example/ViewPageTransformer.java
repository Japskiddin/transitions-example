package ru.androidtools.transitions_example;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class ViewPageTransformer implements ViewPager.PageTransformer {
  public ViewPageTransformer() {
  }

  private static final float MIN_SCALE_ZOOM = 0.85f;
  private static final float MIN_ALPHA_ZOOM = 0.5f;

  public void transformPage(@NonNull View page, float position) {
    float alpha = 0;
    float scale = 0;
    float translationX = 0;

    if (position >= -1 && position <= 1) {
      scale = Math.max(MIN_SCALE_ZOOM, 1 - Math.abs(position));
      alpha =
          MIN_ALPHA_ZOOM + (scale - MIN_SCALE_ZOOM) / (1 - MIN_SCALE_ZOOM) * (1 - MIN_ALPHA_ZOOM);
      float vMargin = page.getHeight() * (1 - scale) / 2;
      float hMargin = page.getWidth() * (1 - scale) / 2;
      if (position < 0) {
        translationX = (hMargin - vMargin / 2);
      } else {
        translationX = (-hMargin + vMargin / 2);
      }
    } else {
      alpha = 1;
      scale = 1;
      translationX = 0;
    }

    page.setAlpha(alpha);
    page.setTranslationX(translationX);
    page.setScaleX(scale);
    page.setScaleY(scale);
  }
}
