package com.hdvideoplayer.smartplayer.player.adapters.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import java.util.ArrayList;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.activities.VideoPlayerActivity;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.util.Utility;

public class VideoPlayerListAdapter extends RecyclerView.Adapter<VideoPlayerListAdapter.VideoViewHolder> {
    private List<VideoInfo> videoList;
    private Context context;
      public VideoPlayerListAdapter(Context context) {
        this.context = context;
        this.videoList = new ArrayList<>();
    }

    public void updateVideoDataList(List<VideoInfo> newVideoList) {
        videoList.clear();
        videoList.addAll(newVideoList);
        notifyDataSetChanged();
    }@NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_videoplayer_list, parent, false);
        return new VideoViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoInfo videoInfo = videoList.get(position);

        ((RequestBuilder) Glide.with(this.context).load(videoInfo.getPath()).centerCrop()).into(holder.ivThumbnail);
        holder.tvVideoName.setText(videoInfo.getDisplayName());
        holder.tvCreatedDay.setText(Utility.convertSize(videoInfo.getSize()) + " | " + Utility.convertLongToTime(videoInfo.getDate(), "dd-MM-yyyy"));
        holder.tvTotalTime.setText(Utility.convertLongToDuration(videoInfo.getDuration()));

        holder.itemView.setOnClickListener(v -> {
            VideoInfo selectedVideo = videoList.get(position);
            ((VideoPlayerActivity) context).playVideo(selectedVideo);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivThumbnail;
        private TextView tvCreatedDay;
        private TextView tvTotalTime;
        private TextView tvVideoName;

        public VideoViewHolder(View view) {
            super(view);
            this.ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            this.tvVideoName = (TextView) view.findViewById(R.id.tv_video_name);
            this.tvCreatedDay = (TextView) view.findViewById(R.id.tv_created_day);
            this.tvTotalTime = (TextView) view.findViewById(R.id.tv_total_time);
            this.tvCreatedDay.setSelected(true);
            this.tvVideoName.setSelected(true);
            this.tvTotalTime.setSelected(true);
        }
    }
}