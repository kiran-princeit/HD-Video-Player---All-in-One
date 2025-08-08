package com.hdvideoplayer.smartplayer.player.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.R;

public class BottomMenuAdapter extends Adapter<BottomMenuAdapter.ViewHolder> {
    private Callback mCallback;
    private List<Integer> mIcons;
    private List<Integer> mSelections;

    public interface Callback {
        void onClick(int i);
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvSelectionName;

        public ViewHolder(View view) {
            super(view);
            this.tvSelectionName = (TextView) view.findViewById(R.id.tv_selection_name);
            this.ivIcon = (ImageView) view.findViewById(R.id.iv_icon_option);
        }
    }

    public BottomMenuAdapter(List<Integer> list, List<Integer> list2, Callback callback) {
        this.mSelections = list;
        this.mCallback = callback;
        this.mIcons = list2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_option_bottom, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.tvSelectionName.setText(((Integer) this.mSelections.get(i)).intValue());
        viewHolder.ivIcon.setImageResource(((Integer) this.mIcons.get(i)).intValue());
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                BottomMenuAdapter.this.m563x1bac0e6e(i, view);
            }
        });
    }

    public void m563x1bac0e6e(int i, View view) {
        this.mCallback.onClick(i);
    }

    public int getItemCount() {
        return this.mSelections.size();
    }
}
