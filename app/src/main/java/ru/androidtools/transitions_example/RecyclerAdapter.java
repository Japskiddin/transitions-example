package ru.androidtools.transitions_example;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
  public interface ClickListener {
    void onItemClick(View view, int pos);
  }

  private List<String> list;
  private ClickListener listener;

  public RecyclerAdapter(List<String> list, ClickListener listener) {
    this.list = new ArrayList<>(list);
    this.listener = listener;
  }

  @NonNull @Override
  public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.
        from(parent.getContext()).
        inflate(R.layout.recycler_item, parent, false);
    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
    String text = list.get(position);
    holder.bind(text, listener, position);
  }

  @Override public int getItemCount() {
    return list.size();
  }

  public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private TextView tv_item;
    private LinearLayout card_layout;

    RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      tv_item = itemView.findViewById(R.id.tv_item);
      card_layout = itemView.findViewById(R.id.card_layout);
    }

    void bind(final String text, final ClickListener listener, final int pos) {
      tv_item.setText(text);
      tv_item.setTransitionName(tv_item.getContext().getString(R.string.transition_name, pos));
      card_layout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          listener.onItemClick(tv_item, pos);
        }
      });
    }

    public View getView() {
      return tv_item;
    }
  }
}
