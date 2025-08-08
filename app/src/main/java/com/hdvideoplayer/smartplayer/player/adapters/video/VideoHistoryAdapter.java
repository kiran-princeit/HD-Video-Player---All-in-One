package com.hdvideoplayer.smartplayer.player.adapters.video;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;

import java.util.ArrayList;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.activities.VideoPlayerActivity;
import com.hdvideoplayer.smartplayer.player.adapters.BottomMenuAdapter;
import com.hdvideoplayer.smartplayer.player.adsprosimple.AdManager;
import com.hdvideoplayer.smartplayer.player.adsprosimple.OnActivityResultLauncher1;
import com.hdvideoplayer.smartplayer.player.data.datasource.VideoDatabaseControl;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoHistory;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.dialog.BottomMenuDialogControl;
import com.hdvideoplayer.smartplayer.player.util.FirebaseAnalyticsUtils;
import com.hdvideoplayer.smartplayer.player.util.Utility;
import com.hdvideoplayer.smartplayer.player.util.constant.AppConstant;

public class VideoHistoryAdapter extends Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder> {
    private Activity activity;
    private Callback mCallback;
    private List<VideoHistory> mHistories;
    private List<Long> mVideoIds = new ArrayList();

    public interface Callback {
        void onHistoryOptionSelect(VideoHistory videoHistory, int i, int i2);
    }

    public static class EmptyViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public EmptyViewHolder(View view) {
            super(view);
        }
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private ImageView ivMore;
        private ImageView ivThumbnail;
        private TextView tvCreatedDay;
        private TextView tvTotalTime;
        private TextView tvVideoName;

        public ViewHolder(View view) {
            super(view);
            this.ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            this.tvVideoName = (TextView) view.findViewById(R.id.tv_video_name);
            this.tvCreatedDay = (TextView) view.findViewById(R.id.tv_created_day);
            this.tvTotalTime = (TextView) view.findViewById(R.id.tv_total_time);
            this.ivMore = (ImageView) view.findViewById(R.id.iv_more);
        }
    }

    public VideoHistoryAdapter(Activity activity, List<VideoHistory> list, Callback callback) {
        this.activity = activity;
        this.mHistories = list;
        this.mCallback = callback;
    }


    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == -1) {
            return new EmptyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_empty_data, viewGroup, false));
        }
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder.getItemViewType() != -1) {
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            final VideoHistory videoHistory = (VideoHistory) this.mHistories.get(i);
            VideoInfo video = videoHistory.getVideo();
            Log.e("VideoHistoryAdapter", "Binding video: " + video.getDisplayName());
            Glide.with(this.activity).load(video.getPath()).into(viewHolder2.ivThumbnail);
            viewHolder2.tvVideoName.setText(video.getDisplayName());
            viewHolder2.tvCreatedDay.setText(Utility.convertLongToTime(videoHistory.getDateAdded(), "dd-MM-yyyy"));
            viewHolder2.tvTotalTime.setText(Utility.convertLongToDuration(video.getDuration()));


            viewHolder2.itemView.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    AdManager.showInterstitial(activity, new OnActivityResultLauncher1.OnActivityResultLauncher2() {
                        @Override
                        public void onLauncher() {
                            Intent intent = new Intent(activity, VideoPlayerActivity.class);
                            intent.putExtra(AppConstant.IntentExtra.EXTRA_VIDEO_NUMBER, i);
                            VideoPlayerActivity.sVideoList = new

                                    ArrayList(getAllVideo());
                            intent.addFlags(C.ENCODING_PCM_32BIT);
                            activity.startActivity(intent);
                            FirebaseAnalyticsUtils.putEventClick(activity, FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_HISTORY, "click_item_file");
                        }
                    });
                }

            });
            viewHolder2.ivMore.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    BottomMenuDialogControl.getInstance().showMoreDialogHistory(activity, new BottomMenuAdapter.Callback() {
                        public final void onClick(int i) {
                            mCallback.onHistoryOptionSelect(videoHistory, i, i);
                        }
                    });
                    FirebaseAnalyticsUtils.putEventClick(activity, FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_HISTORY, "click_more_video_history");
                }
            });
        }
    }

    public int getItemViewType(int i) {
        List list = this.mHistories;
        return (list == null || list.isEmpty()) ? -1 : 0;
    }

    public int getItemCount() {
        List list = this.mHistories;
        return (list == null || list.isEmpty()) ? 1 : this.mHistories.size();
    }

    public void updateHistory(List<VideoHistory> videos) {
        this.mHistories = videos;
        notifyDataSetChanged();
    }

    public void removeItemPosition(int position) {
        if (position >= 0 && position < mHistories.size()) {
            mHistories.remove(position);
            notifyItemRemoved(position);
        }
    }

    private List<VideoInfo> getAllVideo() {
        ArrayList arrayList = new ArrayList();
        List<VideoHistory> list = this.mHistories;
        if (list == null) {
            return new ArrayList();
        }
        for (VideoHistory id : list) {
            VideoInfo videoById = VideoDatabaseControl.getInstance().getVideoById(id.getId());
            if (videoById != null) {
                arrayList.add(videoById);
            }
        }
        return arrayList;
    }
}
