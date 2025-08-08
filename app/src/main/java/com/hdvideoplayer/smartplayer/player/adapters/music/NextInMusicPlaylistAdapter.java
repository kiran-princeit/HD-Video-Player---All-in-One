package com.hdvideoplayer.smartplayer.player.adapters.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.util.Utility;
import com.hdvideoplayer.smartplayer.player.util.music.MusicPlayerUtils;

public class NextInMusicPlaylistAdapter extends Adapter<NextInMusicPlaylistAdapter.ViewHolder> {
    private Callback mCallback;
    private Context mContext;
    private int mCurrentPosition;
    private List<MusicInfo> mMusics;

    public interface Callback {
        void onMusicPlay(int i);
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private ImageView ivThumbnail;
        private TextView tvArtist;
        private TextView tvDuration;
        private TextView tvSong;

        public ViewHolder(View view) {
            super(view);
            this.ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            this.tvSong = (TextView) view.findViewById(R.id.tv_song);
            this.tvArtist = (TextView) view.findViewById(R.id.tv_artist);
            this.tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        }
    }

    public NextInMusicPlaylistAdapter(Context context, List<MusicInfo> list, int i, Callback callback) {
        this.mContext = context;
        this.mMusics = list;
        this.mCurrentPosition = i;
        this.mCallback = callback;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music_info, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        if (this.mCurrentPosition == i) {
            viewHolder.tvSong.setTextColor(this.mContext.getResources().getColor(R.color.app_color));
            viewHolder.tvArtist.setTextColor(this.mContext.getResources().getColor(R.color.app_color));
            viewHolder.tvDuration.setTextColor(this.mContext.getResources().getColor(R.color.app_color));
        } else {
            viewHolder.tvSong.setTextColor(this.mContext.getResources().getColor(R.color.color_B0B0B0));
            viewHolder.tvArtist.setTextColor(this.mContext.getResources().getColor(R.color.color_B0B0B0));
            viewHolder.tvDuration.setTextColor(this.mContext.getResources().getColor(R.color.color_B0B0B0));
        }
        MusicInfo musicInfo = (MusicInfo) this.mMusics.get(i);
        viewHolder.tvSong.setText(musicInfo.getDisplayName());
        viewHolder.tvArtist.setText(musicInfo.getArtist());
        viewHolder.tvDuration.setText(Utility.convertLongToDuration(musicInfo.getDuration()));
        ((RequestBuilder) ((RequestBuilder) ((RequestBuilder) Glide.with(this.mContext).load(MusicPlayerUtils.getThumbnailOfSong(this.mContext, musicInfo.getPath(), 60)).placeholder(R.drawable.ic_music_icon)).centerCrop()).error(R.drawable.ic_music_icon)).into(viewHolder.ivThumbnail);
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                mCallback.onMusicPlay(i);
            }
        });
    }
    public void updateCurrentPosition(int i) {
        this.mCurrentPosition = i;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        List list = this.mMusics;
        if (list == null) {
            return 0;
        }
        return list.size();
    }
}
