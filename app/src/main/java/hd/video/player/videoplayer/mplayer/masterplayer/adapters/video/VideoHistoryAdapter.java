package hd.video.player.videoplayer.mplayer.masterplayer.adapters.video;

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
import com.bumptech.glide.RequestBuilder;
import com.google.android.exoplayer2.C;

import java.util.ArrayList;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.activities.VideoPlayerActivity;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.VideoDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoHistory;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant.IntentExtra;

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
//        for (VideoHistory id : list) {
//            this.mVideoIds.add(Long.valueOf(id.getId()));
//        }
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
                    VideoHistoryAdapter.this.m588x745568cd(i, view);
                }
            });
            viewHolder2.ivMore.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    VideoHistoryAdapter.this.m590xc34d1c4f(videoHistory, i, view);
                }
            });
        }
    }

    public void m588x745568cd(int i, View view) {
        Intent intent = new Intent(this.activity, VideoPlayerActivity.class);
        intent.putExtra(IntentExtra.EXTRA_VIDEO_NUMBER, i);
        VideoPlayerActivity.sVideoList = new ArrayList(getAllVideo());
        intent.addFlags(C.ENCODING_PCM_32BIT);
        activity.startActivity(intent);
        FirebaseAnalyticsUtils.putEventClick(this.activity, FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_HISTORY, "click_item_file");
    }

    public void m589x1bd1428e(VideoHistory videoHistory, int i, int i2) {
        this.mCallback.onHistoryOptionSelect(videoHistory, i2, i);
    }

    public void m590xc34d1c4f(final VideoHistory videoHistory, final int i, View view) {
        BottomMenuDialogControl.getInstance().showMoreDialogHistory(this.activity, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                VideoHistoryAdapter.this.m589x1bd1428e(videoHistory, i, i);
            }
        });
        FirebaseAnalyticsUtils.putEventClick(this.activity, FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_HISTORY, "click_more_video_history");
    }

    public int getItemViewType(int i) {
        List list = this.mHistories;
        return (list == null || list.isEmpty()) ? -1 : 0;
    }

    public int getItemCount() {
        List list = this.mHistories;
        return (list == null || list.isEmpty()) ? 1 : this.mHistories.size();
    }

//    public void removeItemPosition(int i) {
//        this.mHistories.remove(i);
//        notifyItemRemoved(i);
//        notifyItemRangeChanged(i, getItemCount());
//    }

    public void removeAllItem() {
        this.mHistories = new ArrayList();
        notifyDataSetChanged();
    }

//    public void updateHistory(List<VideoHistory> list) {
//        this.mHistories = list;
//        notifyDataSetChanged();
//        this.mVideoIds.clear();
//        for (VideoHistory id : this.mHistories) {
//            this.mVideoIds.add(Long.valueOf(id.getId()));
//        }
//    }

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
