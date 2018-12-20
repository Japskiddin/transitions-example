package ru.androidtools.transitions_example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
  public interface PagerListener {
    void setStartPostTransition(View view);
  }

  private List<String> list;
  private int current;
  private PagerListener listener;
  private Context context;
  private View mCurrentView;
  View mPreviousView;

  public ViewPagerAdapter(List<String> list, int current, Context context, PagerListener listener) {
    this.list = new ArrayList<>(list);
    this.current = current;
    this.listener = listener;
    this.context = context;
  }

  @NonNull
  @Override public Object instantiateItem(@NonNull ViewGroup container, int position) {
    String text = list.get(position);
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.pager_item, container, false);
    TextView tv_item = view.findViewById(R.id.tv_item);
    tv_item.setText(text);

    container.addView(view);

    String name = context.getString(R.string.transition_name, position);
    tv_item.setTag(name);
    if (position == current) {
      listener.setStartPostTransition(tv_item);
    }

    return view;
  }

  @Override public int getCount() {
    return list.size();
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    container.removeView((View) object);
    if (object == mPreviousView) {
      mPreviousView = null;
    }
  }

  @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    return view == object;
  }

  public View getCurrentView() {
    return mCurrentView;
  }

  @Override
  public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    if (mPreviousView != null) {
      ViewCompat.setTransitionName(mPreviousView, null);
      ViewCompat.setTransitionName(mPreviousView.findViewById(R.id.tv_item), null);
    }
    mPreviousView = mCurrentView;
    mCurrentView = (View) object;
    ViewCompat.setTransitionName(mCurrentView, context.getString(R.string.transition_name_container, position));
    ViewCompat.setTransitionName(mCurrentView.findViewById(R.id.tv_item),
            context.getString(R.string.transition_name, position));
  }
}
