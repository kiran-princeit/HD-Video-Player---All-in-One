package hd.video.player.videoplayer.mplayer.masterplayer.adapters.video;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.VideoDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoFolder;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;

public class VideoFoldersAdapter extends Adapter<VideoFoldersAdapter.ViewHolder> {
    Activity activity;
    private final Callback mCallback;
    private final Context mContext;
    private final boolean mIsSelectMode;
    private List<VideoFolder> mVideoFolders = new ArrayList();
    private int mViewMode = 1;

    public interface Callback {
        void onFolderClick(VideoFolder videoFolder, int i);

        void onFolderOptionSelect(VideoFolder videoFolder, int i);
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ImageView ivMore;
        TextView tvFolderName;
        TextView tvNumber;

        public ViewHolder(View view) {
            super(view);
            this.tvFolderName = (TextView) view.findViewById(R.id.tv_folder_name);
            this.tvNumber = (TextView) view.findViewById(R.id.tv_number);
            this.ivMore = (ImageView) view.findViewById(R.id.iv_more);
        }
    }

    public VideoFoldersAdapter(Context context, boolean z, Callback callback, Activity activity) {
        this.mContext = context;
        this.mCallback = callback;
        this.mIsSelectMode = z;
        this.activity = activity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == 1) {
            // Inflate ListView item layout (single row)
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folder_list, viewGroup, false);
            return new ViewHolder(view);
        } else if (viewType == 2) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folder_grid, viewGroup, false);
            return new ViewHolder(view);
        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {
        if (mViewMode == 2) {
            return 2; // For GridView mode
        }
        return 1; // For ListView mode
    }


    public int getItemCount() {
        return this.mVideoFolders.size();
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        if (i == 0) {
            int size;
            if (this.mIsSelectMode) {
                viewHolder.tvFolderName.setText("All videos");
                size = VideoDatabaseControl.getInstance().getAllVideos().size();
                viewHolder.tvNumber.setText(this.mContext.getResources().getQuantityString(R.plurals.value_of_video, size, new Object[]{Integer.valueOf(size)}));
            } else {
                viewHolder.tvFolderName.setText(R.string.recently_added);
                size = VideoDatabaseControl.getInstance().getAllRecentlyVideo().size();
                viewHolder.tvNumber.setText(this.mContext.getResources().getQuantityString(R.plurals.value_of_video, size, new Object[]{Integer.valueOf(size)}));
            }
            viewHolder.ivMore.setVisibility(8);
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    VideoFoldersAdapter.this.folderRecentClick(i, view);
                }
            });
            return;
        }
        if (this.mIsSelectMode) {
            viewHolder.ivMore.setVisibility(8);
        } else {
            viewHolder.ivMore.setVisibility(0);
        }
        final VideoFolder videoFolder = (VideoFolder) this.mVideoFolders.get(i);
        viewHolder.tvFolderName.setText(videoFolder.getFolderName());
        viewHolder.tvNumber.setText(this.mContext.getResources().getQuantityString(R.plurals.value_of_video, videoFolder.getVideoList().size(), new Object[]{Integer.valueOf(videoFolder.getVideoList().size())}));
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                VideoFoldersAdapter.this.folderClick(videoFolder, i, view);
            }
        });
        viewHolder.ivMore.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                VideoFoldersAdapter.this.onFolderOptionClick(videoFolder, i, view);
            }
        });
    }

    public void folderRecentClick(int i, View view) {
        this.mCallback.onFolderClick(new VideoFolder(), i);
        FirebaseAnalyticsUtils.putEventClick(this.mContext, FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_FOLDER, "click_recently");
    }

    public void folderClick(VideoFolder videoFolder, int i, View view) {
        this.mCallback.onFolderClick(videoFolder, i);
        FirebaseAnalyticsUtils.putEventClick(this.mContext, FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_FOLDER, "click_video_folder");
    }

    public void onFolderOptionClick(VideoFolder videoFolder, int i, View view) {
        this.mCallback.onFolderOptionSelect(videoFolder, i);
        FirebaseAnalyticsUtils.putEventClick(this.mContext, FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_FOLDER, "click_more_folder");
    }

    public void removeItemPosition(int i) {
        if (i >= 0 && i < this.mVideoFolders.size()) {
            this.mVideoFolders.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, getItemCount());
        }
    }

    public void updateRecently() {
        notifyItemChanged(0);
    }
    public void updateVideoFolders(List<VideoFolder> folders) {
        this.mVideoFolders.clear();
        if (folders != null && !folders.isEmpty()) {
            this.mVideoFolders.addAll(folders);
        }
        notifyDataSetChanged();
    }


    public void setViewMode(int i) {
        if (this.mViewMode != i) {
            this.mViewMode = i;
            notifyDataSetChanged();
        }
    }
}
