package ru.androidtools.transitions_example.two_activitiws;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;
import ru.androidtools.transitions_example.R;
import ru.androidtools.transitions_example.RecyclerAdapter;

import static ru.androidtools.transitions_example.Constants.EXTRA_CURRENT;
import static ru.androidtools.transitions_example.Constants.EXTRA_EXIT;
import static ru.androidtools.transitions_example.Tools.getList;

public class FirstActivity extends Activity {
  private List<String> test_list;
  private int exit_position;
  private RecyclerView recyclerView;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_first);
    recyclerView = findViewById(R.id.recycler_view);
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    } else {
      recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }
    recyclerView.setHasFixedSize(true);
    recyclerView.setItemViewCacheSize(10);
    recyclerView.setDrawingCacheEnabled(true);
    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    test_list = getList();
    RecyclerAdapter adapter = new RecyclerAdapter(test_list, new RecyclerAdapter.ClickListener() {
      @Override public void onItemClick(View view, int pos) {
        Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
        //??? intent.putExtra("current", (int) image.getTag());
        intent.putExtra(EXTRA_CURRENT, pos);
        ActivityOptions options =
            ActivityOptions.makeSceneTransitionAnimation(FirstActivity.this, view,
                view.getTransitionName());
        startActivity(intent, options.toBundle());
      }
    });
    recyclerView.setAdapter(adapter);
  }

  @Override public void onActivityReenter(int resultCode, Intent data) {
    super.onActivityReenter(resultCode, data);
    if (resultCode == RESULT_OK && data != null) {
      exit_position = data.getIntExtra(EXTRA_EXIT, 0);
      final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
      View viewAtPosition = layoutManager.findViewByPosition(exit_position);
      if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false,
          true)) {
        layoutManager.scrollToPosition(exit_position);
        setTransitionOnViewWithListener();
      }
      // карточка видна, нужно поставить колбек, он мог уже быть установленным
      else {
        setTransitionOnView();
      }
    }
  }

  private void setTransitionOnView() {
    final CustomSharedElementCallback callback = new CustomSharedElementCallback();
    setExitSharedElementCallback(callback);

    addTransitionListener();
    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(exit_position);
    if (holder instanceof RecyclerAdapter.RecyclerViewHolder) {
      callback.setView(((RecyclerAdapter.RecyclerViewHolder) holder).getView());
    }
  }

  private void setTransitionOnViewWithListener() {
    final CustomSharedElementCallback callback = new CustomSharedElementCallback();
    setExitSharedElementCallback(callback);

    addTransitionListener();
    postponeEnterTransition();

    recyclerView.getViewTreeObserver()
        .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
          @Override public boolean onPreDraw() {
            recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

            RecyclerView.ViewHolder holder =
                recyclerView.findViewHolderForAdapterPosition(exit_position);
            if (holder instanceof RecyclerAdapter.RecyclerViewHolder) {
              callback.setView(((RecyclerAdapter.RecyclerViewHolder) holder).getView());
            }
            startPostponedEnterTransition();

            return true;
          }
        });
  }

  private void addTransitionListener() {
    getWindow().getSharedElementExitTransition().addListener(new Transition.TransitionListener() {
      @Override public void onTransitionStart(Transition transition) {
      }

      @Override public void onTransitionPause(Transition transition) {
      }

      @Override public void onTransitionResume(Transition transition) {
      }

      @Override public void onTransitionEnd(Transition transition) {
        removeCallback();
      }

      @Override public void onTransitionCancel(Transition transition) {
        removeCallback();
      }

      private void removeCallback() {
        getWindow().getSharedElementExitTransition().removeListener(this);
        setExitSharedElementCallback(null);
      }
    });
  }

  private static class CustomSharedElementCallback extends SharedElementCallback {
    private View mView;

    public void setView(View view) {
      mView = view;
    }

    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
      // Clear all current shared views and names
      names.clear();
      sharedElements.clear();

      if (mView != null) {
        // костыль NullPointerException: at android.support.v4.view.ViewCompat.getTransitionName
        // Store new selected view and name
        String transitionName = ViewCompat.getTransitionName(mView);
        names.add(transitionName);
        sharedElements.put(transitionName, mView);
      }
    }
  }
}
