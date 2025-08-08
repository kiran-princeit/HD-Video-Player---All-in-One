package com.hdvideoplayer.smartplayer.player.adapters.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.R;
public class SoundPresetAdapter extends RecyclerView.Adapter<SoundPresetAdapter.ViewHolder> {
    private Callback mCallback;
    private Context mContext;
    private int mCurrentSelect = 0;
    private List<String> mPresets;

    public interface Callback {
        void onSelect(int i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_preset);
        }
    }

    public SoundPresetAdapter(Context context, List<String> list, Callback callback, int initialSelect) {
        this.mContext = context;
        this.mCallback = callback;
        this.mPresets = new ArrayList<>();
        this.mPresets.add(context.getString(R.string.custom));  // Add "custom" preset
        this.mPresets.addAll(list);  // Add presets from the passed list
        this.mCurrentSelect = initialSelect;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sound_preset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvName.setText(mPresets.get(position));
        if (mCurrentSelect == position) {
            holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.itemView.setBackgroundResource(R.drawable.bg_music_preset_selected);
        } else {
            holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.app_color));
            holder.itemView.setBackgroundResource(R.drawable.bg_music_preset_unselected);
        }
        holder.itemView.setOnClickListener(view -> m579x2666ee80(position, view));
    }

    public void m579x2666ee80(int position, View view) {
        if (mCurrentSelect != position) {
            int oldPosition = mCurrentSelect;
            mCallback.onSelect(position);
            mCurrentSelect = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(position);
        }
    }

    public void setCurrentSelect(int position) {
        mCurrentSelect = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPresets != null ? mPresets.size() : 0;
    }
}

