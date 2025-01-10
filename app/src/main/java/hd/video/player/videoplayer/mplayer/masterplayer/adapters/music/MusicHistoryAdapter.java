package hd.video.player.videoplayer.mplayer.masterplayer.adapters.music;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.activities.PlayMusicActivity;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicHistory;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant.IntentExtra;
import hd.video.player.videoplayer.mplayer.masterplayer.util.music.MusicPlayerUtils;


public class MusicHistoryAdapter extends Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder> {
    private Activity mActivity;
    private Callback mCallback;
    private List<MusicHistory> mMusicHistoryList;
    private List<Long> mMusicIdList = new ArrayList<>();

    public interface Callback {
        void onMusicOptionSelected(MusicHistory musicHistory, int position, int optionId);
    }

    public static class EmptyViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public EmptyViewHolder(View view) {
            super(view);
        }
    }

    public static class MusicViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private ImageView ivThumbnail;
        private TextView tvSongTitle;
        private TextView tvArtistName;
        private TextView tvSongDuration;
        private ImageView ivOptions;

        public MusicViewHolder(View view) {
            super(view);
            this.ivThumbnail = view.findViewById(R.id.iv_thumbnail);
            this.tvSongTitle = view.findViewById(R.id.tv_song);
            this.tvArtistName = view.findViewById(R.id.tv_artist);
            this.tvSongDuration = view.findViewById(R.id.tv_duration);
            this.ivOptions = view.findViewById(R.id.iv_more);
        }
    }

    public MusicHistoryAdapter(Activity activity, List<MusicHistory> musicHistoryList, Callback callback) {
        this.mActivity = activity;
        this.mMusicHistoryList = musicHistoryList;
        this.mCallback = callback;
        for (MusicHistory musicHistory : musicHistoryList) {
            this.mMusicIdList.add(musicHistory.getId());
        }
    }

    @Override
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != -1) {
            return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_info, parent, false));
        }
        View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_data, parent, false);
        ((TextView) emptyView.findViewById(R.id.tv_history)).setText(R.string.no_song_listened);
        return new EmptyViewHolder(emptyView);
    }

    @Override
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() != -1) {
            MusicViewHolder musicViewHolder = (MusicViewHolder) holder;
            final MusicHistory musicHistory = mMusicHistoryList.get(position);
            MusicInfo musicInfo = musicHistory.getMusics();

            musicViewHolder.tvSongTitle.setText(musicInfo.getDisplayName());
            musicViewHolder.tvArtistName.setText(musicInfo.getArtist());
            musicViewHolder.tvSongDuration.setText(Utility.convertLongToDuration(musicInfo.getDuration()));

            Glide.with(mActivity)
                    .load(MusicPlayerUtils.getThumbnailOfSong(mActivity, musicInfo.getPath(), 60))
                    .placeholder(R.drawable.ic_music_icon)
                    .centerCrop()
                    .error(R.drawable.ic_music_icon)
                    .into(musicViewHolder.ivThumbnail);

            musicViewHolder.itemView.setOnClickListener(view -> navigateToMusicPlayer(position, view));
            musicViewHolder.ivOptions.setOnClickListener(view -> showOptionsMenu(musicHistory, position, view));
        }
    }

    private void navigateToMusicPlayer(int position, View view) {
        Intent intent = new Intent(view.getContext(), PlayMusicActivity.class);
        intent.putExtra(IntentExtra.EXTRA_MUSIC_NUMBER, position);
        intent.putExtra(IntentExtra.EXTRA_MUSIC_ARRAY, (ArrayList<Long>) mMusicIdList);
        intent.addFlags(C.ENCODING_PCM_32BIT);
        mActivity.startActivity(intent);
        FirebaseAnalyticsUtils.putEventClick(mActivity, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_HISTORY, "click_item_file");
    }

    private void showOptionsMenu(final MusicHistory musicHistory, final int position, View view) {
        BottomMenuDialogControl.getInstance().showMoreDialogHistory(mActivity, new BottomMenuAdapter.Callback() {
            @Override
            public void onClick(int optionId) {
                mCallback.onMusicOptionSelected(musicHistory, position, optionId);
            }
        });
        FirebaseAnalyticsUtils.putEventClick(mActivity, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_HISTORY, "click_more_music_history");
    }

    @Override
    public int getItemViewType(int position) {
        return (mMusicHistoryList == null || mMusicHistoryList.isEmpty()) ? -1 : 0;
    }

    @Override
    public int getItemCount() {
        return (mMusicHistoryList == null || mMusicHistoryList.isEmpty()) ? 1 : mMusicHistoryList.size();
    }

    public void removeItemAtPosition(int position) {
        mMusicHistoryList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void updateMusicHistoryList(List<MusicHistory> newMusicHistoryList) {
        mMusicHistoryList = newMusicHistoryList;
        notifyDataSetChanged();
        mMusicIdList.clear();
        for (MusicHistory musicHistory : mMusicHistoryList) {
            mMusicIdList.add(musicHistory.getId());
        }
    }

        public void removeAllItems() {
        this.mMusicHistoryList = new ArrayList();
        notifyDataSetChanged();
    }
}
