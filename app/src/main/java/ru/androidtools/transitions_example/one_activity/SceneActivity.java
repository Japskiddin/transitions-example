package ru.androidtools.transitions_example.one_activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.Scene;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;
import androidx.viewpager.widget.ViewPager;
import java.util.List;
import ru.androidtools.transitions_example.R;
import ru.androidtools.transitions_example.RecyclerAdapter;
import ru.androidtools.transitions_example.ViewPageTransformer;
import ru.androidtools.transitions_example.ViewPagerAdapter;

import static ru.androidtools.transitions_example.Tools.getList;

public class SceneActivity extends Activity {
  private View include_recycler, include_pager;
  private FrameLayout frameLayout;
  private RecyclerView recyclerView;
  private ViewPager viewPager;
  private List<String> list;
  private int recycler_position, pager_position, current_view = 0;
  private boolean animated = false;
  private ViewPagerAdapter pagerAdapter;
  private RecyclerAdapter recyclerAdapter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scene);
    frameLayout = findViewById(R.id.frame_layout);
    include_recycler = getLayoutInflater().inflate(R.layout.include_recycler, frameLayout, false);
    include_pager = getLayoutInflater().inflate(R.layout.include_pager, frameLayout, false);
    setupRecyclerView();
    setupViewPager();

    list = getList();
    goRecyclerView();
  }

  private void setupRecyclerView() {
    recyclerView = include_recycler.findViewById(R.id.recycler_view);
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    } else {
      recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }
    recyclerView.setHasFixedSize(true);
    recyclerView.setItemViewCacheSize(10);
    recyclerView.setDrawingCacheEnabled(true);
    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
  }

  private void setupViewPager() {
    viewPager = include_pager.findViewById(R.id.view_pager);
    viewPager.setPageTransformer(true, new ViewPageTransformer());
    viewPager.addOnPageChangeListener(page_listener);
  }

  private void goRecyclerView() {
    Scene scene = new Scene(frameLayout, include_recycler);
    scene.enter();
    recyclerAdapter = new RecyclerAdapter(list, new RecyclerAdapter.ClickListener() {
      @Override public void onItemClick(View view, int pos) {
        recycler_position = pos;
        goViewPager();
      }
    });
    recyclerView.setAdapter(recyclerAdapter);
  }

  private void goViewPager() {
    animated = false;
    current_view = 1;
    pagerAdapter =
        new ViewPagerAdapter(list, recycler_position, this, new ViewPagerAdapter.PagerListener() {
          @Override public void setStartPostTransition(View view) {
          }
        });
    viewPager.setAdapter(pagerAdapter);
    viewPager.setCurrentItem(recycler_position);
    if (recycler_position == 0) {
      checkPagerPosition(0);
    }
  }

  ViewPager.OnPageChangeListener page_listener = new ViewPager.OnPageChangeListener() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override public void onPageSelected(int position) {
      checkPagerPosition(position);
    }

    @Override public void onPageScrollStateChanged(int state) {
    }
  };

  private void checkPagerPosition(int pos) {
    pager_position = pos;
    if (recycler_position == pos && !animated) {
      animateToViewPager();
      animated = true;
    }
  }

  private void animateToViewPager() {
    RecyclerAdapter.RecyclerViewHolder holder =
        (RecyclerAdapter.RecyclerViewHolder) recyclerView.findViewHolderForAdapterPosition(
            recycler_position);
    Transition shared =
        TransitionInflater.from(SceneActivity.this).inflateTransition(android.R.transition.move);
    shared.addTarget(holder.getView().getTransitionName());
    Scene scene = new Scene(frameLayout, include_pager);
    TransitionSet set = new TransitionSet();
    set.addTransition(shared);
    TransitionManager.beginDelayedTransition(frameLayout, set);
    scene.enter();
  }

  private void onDetailBack() {
    final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    View viewAtPosition = layoutManager.findViewByPosition(pager_position);
    if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false,
        true)) {
      layoutManager.scrollToPosition(pager_position);
      Scene main = new Scene(frameLayout, include_pager);
      View view = pagerAdapter.getCurrentView();
      TextView imageView = view.findViewById(R.id.tv_item);
      Transition shared =
          TransitionInflater.from(SceneActivity.this).inflateTransition(android.R.transition.move);
      shared.addTarget(imageView.getTransitionName());
      ChangeBounds changeBounds = new ChangeBounds();
      changeBounds.setStartDelay(300);
      changeBounds.setInterpolator(new AnticipateOvershootInterpolator());
      TransitionSet set = new TransitionSet();
      set.addTransition(shared);
      TransitionManager.beginDelayedTransition(frameLayout, set);
      main.exit();
    } else {
      animateToRecyclerView();
    }

    viewPager.setAdapter(null);
    frameLayout.removeView(include_pager);
    current_view = 0;
  }

  private void animateToRecyclerView() {
    RecyclerAdapter.RecyclerViewHolder holder =
        (RecyclerAdapter.RecyclerViewHolder) recyclerView.findViewHolderForAdapterPosition(
            pager_position);
    Transition shared =
        TransitionInflater.from(SceneActivity.this).inflateTransition(android.R.transition.move);
    shared.addTarget(holder.getView().getTransitionName());
    Scene scene = new Scene(frameLayout, include_recycler);
    TransitionSet set = new TransitionSet();
    set.addTransition(shared);
    TransitionManager.beginDelayedTransition(frameLayout, set);
    scene.enter();
  }

  @Override public void onBackPressed() {
    if (current_view == 1) {
      onDetailBack();
    } else {
      super.onBackPressed();
    }
  }
}
