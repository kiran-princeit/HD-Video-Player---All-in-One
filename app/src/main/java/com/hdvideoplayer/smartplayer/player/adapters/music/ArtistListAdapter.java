package com.hdvideoplayer.smartplayer.player.adapters.music;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.Collections;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adsprosimple.AdManager;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicArtist;

public class ArtistListAdapter extends Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder> {
    private Callback mListener;
    private Context mContext;
    private List<MusicArtist> mArtistList;

    public interface Callback {
        void onArtistSelected(MusicArtist musicArtist);
    }

    public static class EmptyViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public EmptyViewHolder(View view) {
            super(view);
        }
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final TextView artistNameTextView;
        private final TextView trackCountTextView;

        public ViewHolder(View view) {
            super(view);
            this.artistNameTextView = (TextView) view.findViewById(R.id.tv_artist);
            this.trackCountTextView = (TextView) view.findViewById(R.id.tv_number_song);
        }
    }

    public ArtistListAdapter(Context context, List<MusicArtist> artists, Callback listener) {
        this.mContext = context;
        this.mArtistList = artists;
        this.mListener = listener;
    }

    @Override
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType != -1) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_artist, parent, false));
//        }
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_data, parent, false);
//        return new EmptyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position) {
//        if (holder.getItemViewType() != -1) {
        ViewHolder viewHolder = (ViewHolder) holder;
        final MusicArtist artist = mArtistList.get(position);
        viewHolder.artistNameTextView.setText(artist.getArtistName());
        int trackCount = artist.getMusicList().size();
        viewHolder.trackCountTextView.setText(mContext.getResources().getQuantityString(R.plurals.value_of_track, trackCount, trackCount));
        viewHolder.itemView.setOnClickListener(v ->
                AdManager.showInterstitial((Activity) mContext, () -> {
                    onArtistClick(artist, v);
                })
        );
//        }
    }

    private void onArtistClick(MusicArtist musicArtist, View view) {
        mListener.onArtistSelected(musicArtist);
    }

    public void sortArtistsByName() {
        Collections.sort(mArtistList, (artist1, artist2) -> artist1.getArtistName().compareToIgnoreCase(artist2.getArtistName()));
        notifyDataSetChanged();
    }

    public void updateArtistList(List<MusicArtist> artists) {
        this.mArtistList = artists;
        sortArtistsByName();
    }

    @Override
    public int getItemViewType(int position) {
        return (mArtistList == null || mArtistList.isEmpty()) ? -1 : 0;
    }

    @Override
    public int getItemCount() {
        return (mArtistList == null || mArtistList.isEmpty()) ? 1 : mArtistList.size();
    }
}
