package ru.androidtools.transitions_example.two_activitiws;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import java.util.List;
import java.util.Map;
import ru.androidtools.transitions_example.R;
import ru.androidtools.transitions_example.ViewPageTransformer;
import ru.androidtools.transitions_example.ViewPagerAdapter;

import static ru.androidtools.transitions_example.Constants.EXTRA_CURRENT;
import static ru.androidtools.transitions_example.Constants.EXTRA_EXIT;
import static ru.androidtools.transitions_example.Tools.getList;

public class SecondActivity extends Activity {
  private List<String> test_list;
  private int current = 0;
  private ViewPager pager;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    postponeEnterTransition();
    setContentView(R.layout.activity_second);
    test_list = getList();
    if (getIntent().getExtras() != null) {
      current = getIntent().getIntExtra(EXTRA_CURRENT, 0);
    }

    pager = findViewById(R.id.view_pager);
    ViewPagerAdapter adapter =
        new ViewPagerAdapter(test_list, current, this, new ViewPagerAdapter.PagerListener() {
          @Override public void setStartPostTransition(final View view) {
            view.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                  @Override public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    startPostponedEnterTransition();
                    return false;
                  }
                });
          }
        });
    pager.setAdapter(adapter);
    pager.setPageTransformer(true, new ViewPageTransformer());
    pager.setCurrentItem(current);
  }

  @Override public void finishAfterTransition() {
    int pos = pager.getCurrentItem();
    Intent intent = new Intent();
    intent.putExtra(EXTRA_EXIT, pos);
    setResult(RESULT_OK, intent);
    if (current != pos) {
      View view = pager.findViewWithTag(getString(R.string.transition_name, pos));
      setSharedElementCallback(view);
    }
    super.finishAfterTransition();
  }

  private void setSharedElementCallback(final View view) {
    setEnterSharedElementCallback(new SharedElementCallback() {
      @Override
      public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        names.clear();
        sharedElements.clear();
        names.add(view.getTransitionName());
        sharedElements.put(view.getTransitionName(), view);
      }
    });
  }
}
