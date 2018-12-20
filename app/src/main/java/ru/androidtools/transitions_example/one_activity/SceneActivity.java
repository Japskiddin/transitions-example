package ru.androidtools.transitions_example.one_activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeImageTransform;
import androidx.transition.ChangeTransform;
import androidx.transition.Fade;
import androidx.transition.Scene;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;
import androidx.viewpager.widget.ViewPager;
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

    String mainName = "MainParent";
    ViewCompat.setTransitionName(include_recycler, mainName);
    ViewCompat.setTransitionName(include_pager, mainName);
    String recyclerPagerName = "Recycler/Pager";
    ViewCompat.setTransitionName(recyclerView, recyclerPagerName);
    ViewCompat.setTransitionName(viewPager, recyclerPagerName);

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
//    recyclerView.setItemViewCacheSize(10);
//    recyclerView.setDrawingCacheEnabled(true);
//    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
  }

  private void setupViewPager() {
    viewPager = include_pager.findViewById(R.id.view_pager);
    viewPager.setPageTransformer(true, new ViewPageTransformer());
    viewPager.addOnPageChangeListener(page_listener);
  }

  private void goRecyclerView() {
    Scene scene = new Scene(frameLayout, include_recycler);
    TransitionManager.go(scene, null);
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
//    if (recycler_position == 0) {
    checkPagerPosition(recycler_position);
//    }
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
    Transition shared =
            getShared();
    Transition fade = new Fade();
    Scene scene = new Scene(frameLayout, include_pager);
    TransitionSet set = new TransitionSet();
    set.addTransition(shared);
    set.addTransition(fade);
    TransitionManager.go(scene, set);
  }

  private void onDetailBack() {
    final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    View viewAtPosition = layoutManager.findViewByPosition(pager_position);
    if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false,
            true)) {
      layoutManager.scrollToPosition(pager_position);
    }
    animateToRecyclerView();

    viewPager.setAdapter(null);
    current_view = 0;
  }

  private Transition getShared() {
    return new TransitionSet()
            .addTransition(new ChangeBounds())
            .addTransition(new ChangeTransform())
            .addTransition(new ChangeImageTransform());
  }

  private void animateToRecyclerView() {
    Transition shared =
            getShared();
    shared.addTarget(getString(R.string.transition_name, pager_position));
    shared.addTarget(getString(R.string.transition_name_container, pager_position));
    Transition fade = new Fade();
    Scene scene = new Scene(frameLayout, include_recycler);
    TransitionSet set = new TransitionSet();
    set.addTransition(shared);
    set.addTransition(fade);
    TransitionManager.go(scene, set);
  }

  @Override public void onBackPressed() {
    if (current_view == 1) {
      onDetailBack();
    } else {
      super.onBackPressed();
    }
  }
}
