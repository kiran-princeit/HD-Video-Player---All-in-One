package com.hdvideoplayer.smartplayer.player.adapters.music;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.activities.PlayMusicActivity;
import com.hdvideoplayer.smartplayer.player.adsprosimple.AdManager;
import com.hdvideoplayer.smartplayer.player.data.database.MyDatabase;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicPlaylist;
import com.hdvideoplayer.smartplayer.player.util.FirebaseAnalyticsUtils;
import com.hdvideoplayer.smartplayer.player.util.Utility;
import com.hdvideoplayer.smartplayer.player.util.constant.AppConstant.IntentExtra;
import com.hdvideoplayer.smartplayer.player.util.music.MusicPlayerUtils;
import com.hdvideoplayer.smartplayer.player.util.thread.ThreadExecutor;

public class MusicInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Activity activity;
    private boolean isAscending;
    private final MusicInfoCallback callback;
    private final boolean isSelectMusicMode;
    private MusicPlaylist musicPlaylist;
    private List<MusicInfo> musicList;
    private int sortMode;

    public interface MusicInfoCallback {
        void onMoreClick(int position, MusicInfo musicInfo);
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View view) {
            super(view);
        }
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivChecked;
        private ImageView ivMore;
        private ImageView ivThumbnail;
        private TextView tvArtist;
        private TextView tvDuration;
        private TextView tvSong;

        public MusicViewHolder(View view) {
            super(view);
            this.ivThumbnail = view.findViewById(R.id.iv_thumbnail);
            this.tvSong = view.findViewById(R.id.tv_song);
            this.tvArtist = view.findViewById(R.id.tv_artist);
            this.tvDuration = view.findViewById(R.id.tv_duration);
            this.ivMore = view.findViewById(R.id.iv_more);
            this.ivChecked = view.findViewById(R.id.iv_checked);
        }
    }

    public MusicInfoAdapter(Activity activity, boolean isSelectMusicMode, MusicInfoCallback callback, List<MusicInfo> musicList) {
        this.activity = activity;
        this.isSelectMusicMode = isSelectMusicMode;
        this.musicList = (musicList != null) ? musicList : new ArrayList<>();
        this.callback = callback;
        this.sortMode = Utility.getMusicSortMode(activity);
        this.isAscending = Utility.getMusicSortAscending(activity);
        this.musicPlaylist = new MusicPlaylist();
    }

    public static int compareByDateAsc(MusicInfo musicInfo1, MusicInfo musicInfo2) {
        int compare = Long.compare(musicInfo1.getDate(), musicInfo2.getDate());
        if (compare == 0) {
            compare = Long.compare(musicInfo1.getDuration(), musicInfo2.getDuration());
        }
        return compare == 0 ? musicInfo1.getDisplayName().compareToIgnoreCase(musicInfo2.getDisplayName()) : compare;
    }

    public static int compareByDateDesc(MusicInfo musicInfo1, MusicInfo musicInfo2) {
        int compare = Long.compare(musicInfo2.getDate(), musicInfo1.getDate());
        if (compare == 0) {
            compare = Long.compare(musicInfo2.getDuration(), musicInfo1.getDuration());
        }
        return compare == 0 ? musicInfo2.getDisplayName().compareToIgnoreCase(musicInfo1.getDisplayName()) : compare;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != -1) {
            return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_info, parent, false));
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_data, parent, false);
        ((TextView) view.findViewById(R.id.tv_history)).setText(R.string.no_songs);
        return new EmptyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() != -1) {
            final MusicViewHolder musicViewHolder = (MusicViewHolder) holder;
            final MusicInfo musicInfo = musicList.get(position);
            musicViewHolder.tvSong.setText(musicInfo.getDisplayName());
            musicViewHolder.tvArtist.setText(musicInfo.getArtist());
            musicViewHolder.tvDuration.setText(Utility.convertLongToDuration(musicInfo.getDuration()));
            Glide.with(activity).load(MusicPlayerUtils.getThumbnailOfSong(activity, musicInfo.getPath(), 60))
                    .centerCrop()
                    .error(R.drawable.ic_music_icon)
                    .into(musicViewHolder.ivThumbnail);

            musicViewHolder.itemView.setOnClickListener(view ->
                    AdManager.showInterstitial(activity, () -> {
                        openMusicPlayer(musicViewHolder, musicInfo, position, view);
                    }));

            final boolean isSelected = !musicViewHolder.ivChecked.isSelected();
            musicViewHolder.ivChecked.setActivated(isSelected);

            if (isSelectMusicMode) {
                musicViewHolder.ivMore.setVisibility(View.GONE);
                musicViewHolder.ivChecked.setVisibility(View.VISIBLE);
                musicViewHolder.ivChecked.setActivated(musicPlaylist.getMusicIdList().contains(musicInfo.getId()));
            } else {
                musicViewHolder.ivChecked.setVisibility(View.GONE);
                musicViewHolder.ivMore.setVisibility(View.VISIBLE);
                musicViewHolder.ivMore.setOnClickListener(view -> onMoreClick(position, musicInfo, view));
            }
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    private void openMusicPlayer(MusicViewHolder holder, MusicInfo musicInfo, int position, View view) {
        if (!isSelectMusicMode || musicPlaylist == null) {
            Intent intent = new Intent(view.getContext(), PlayMusicActivity.class);
            intent.putExtra(IntentExtra.EXTRA_MUSIC_NUMBER, position);
            intent.putExtra(IntentExtra.EXTRA_MUSIC_ARRAY, (ArrayList) getAllMusicIds());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } else {
            final boolean isActivated = holder.ivChecked.isActivated();
            holder.ivChecked.setActivated(!isActivated);
            final long playlistDateAdded = musicPlaylist.getDateAdded();

            ThreadExecutor.runOnDatabaseThread(() -> updatePlaylist(isActivated, musicInfo, playlistDateAdded));
        }
    }

    private void updatePlaylist(boolean addToPlaylist, MusicInfo musicInfo, long playlistDateAdded) {
        List<Long> musicIdList = musicPlaylist.getMusicIdList();
        if (addToPlaylist) {
            if (!musicIdList.contains(musicInfo.getId())) {
                musicIdList.add(musicInfo.getId());
            }
        } else {
            if (musicIdList.contains(musicInfo.getId())) {
                musicIdList.remove(Long.valueOf(musicInfo.getId()));
            }
        }
        MyDatabase.getInstance(activity).musicPlaylistDAO().updateMusicListForPlaylist(musicPlaylist.getDateAdded(), musicIdList);
    }

    private void onMoreClick(int position, MusicInfo musicInfo, View view) {
        if (callback != null) {
            callback.onMoreClick(position, musicInfo);
        }
        FirebaseAnalyticsUtils.putEventClick(activity, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_LAYOUT, "click_more_music");
    }

    private List<Long> getAllMusicIds() {
        ArrayList<Long> ids = new ArrayList<>();
        for (MusicInfo music : musicList) {
            ids.add(music.getId());
        }
        return ids;
    }

    private void sortMusicList(List<MusicInfo> list, int mode, boolean ascending) {
        Comparator<MusicInfo> comparator = null;

        switch (mode) {
            case 0:
                comparator = ascending ? MusicInfoAdapter::compareByDateAsc : MusicInfoAdapter::compareByDateDesc;
                break;
            case 1:
                comparator = ascending ? (m1, m2) -> m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName())
                        : (m1, m2) -> m2.getDisplayName().compareToIgnoreCase(m1.getDisplayName());
                break;
            case 2:
                comparator = ascending ? (m1, m2) -> Long.compare(m1.getSize(), m2.getSize())
                        : (m1, m2) -> Long.compare(m2.getSize(), m1.getSize());
                break;
            case 3:
                comparator = ascending ? (m1, m2) -> Long.compare(m1.getDuration(), m2.getDuration())
                        : (m1, m2) -> Long.compare(m2.getDuration(), m1.getDuration());
                break;
        }

        if (comparator != null) {
            Collections.sort(list, comparator);
        }
    }

    public void sortMusicList(int mode, boolean ascending) {
        if (this.sortMode != mode || this.isAscending != ascending) {
            this.sortMode = mode;
            this.isAscending = ascending;
            sortMusicList(this.musicList, mode, ascending);
            notifyDataSetChanged();
        }
    }

    public void updateMusicDataList(List<MusicInfo> list) {
        sortMusicList(list, this.sortMode, this.isAscending);
        this.musicList = new ArrayList<>(list);
        notifyDataSetChanged();
    }


    public void removeItemAtPosition(int position) {
        if (position >= 0 && position < musicList.size()) {
            musicList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, musicList.size() - position);
        } else {
            Log.w("MusicInfoAdapter", "removeItemAtPosition: Invalid position = " + position + ", size = " + musicList.size());
        }
    }


//    public void removeItemAtPosition(int position) {
//        musicList.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, getItemCount());
//    }

    public MusicInfo getItem(int position) {
        return (position < 0 || position >= musicList.size()) ? null : musicList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return (musicList == null || musicList.isEmpty()) ? -1 : 0;
    }
}

