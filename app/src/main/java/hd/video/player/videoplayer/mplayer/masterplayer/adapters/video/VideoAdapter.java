package hd.video.player.videoplayer.mplayer.masterplayer.adapters.video;

import android.annotation.SuppressLint;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.activities.VideoPlayerActivity;
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant.IntentExtra;
import hd.video.player.videoplayer.mplayer.masterplayer.util.thread.ThreadExecutor;

public class VideoAdapter extends Adapter<VideoAdapter.ViewHolder> {
    public static final int AD_TYPE = 2;
    public static final int CONTENT_TYPE = 1;
    private final Activity activity;
    private boolean ascending;
    private final Callback mCallback;
    private boolean mIsSelectVideoMode;
    private final Playlist mPlaylist;
    private List<VideoInfo> mVideos = new ArrayList();
    private int mViewMode = 1;
    private int sortMode;

    public interface Callback {
        void onMoreClick(int i, int i2, VideoInfo videoInfo);
    }

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    public class MyViewHolder extends ViewHolder {
        private ImageView ivChecked;
        private ImageView ivMore;
        private ImageView ivThumbnail;
        private TextView tvCreatedDay;
        private TextView tvTotalTime;
        private TextView tvVideoName;

        public MyViewHolder(View view) {
            super(view);
            this.ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            this.tvVideoName = (TextView) view.findViewById(R.id.tv_video_name);
            this.tvCreatedDay = (TextView) view.findViewById(R.id.tv_created_day);
            this.ivMore = (ImageView) view.findViewById(R.id.iv_more);
            this.tvTotalTime = (TextView) view.findViewById(R.id.tv_total_time);
            this.ivChecked = (ImageView) view.findViewById(R.id.iv_checked);
            this.tvCreatedDay.setSelected(true);
            this.tvVideoName.setSelected(true);
            this.tvTotalTime.setSelected(true);
        }
    }

//    public int getItemViewType(int i) {
//        return i;
//    }

    public VideoAdapter(Activity activity, boolean z, Callback callback, Playlist playlist) {
        this.activity = activity;
        this.mPlaylist = playlist;
        this.mIsSelectVideoMode = z;
        this.mCallback = callback;
        this.sortMode = Utility.getVideoSortMode(activity);
        this.ascending = Utility.getVideoSortAscending(activity);
    }

    public static int lambda$sortVideoList$3(VideoInfo videoInfo, VideoInfo videoInfo2) {
        int compare = Long.compare(videoInfo.getDate(), videoInfo2.getDate());
        if (compare == 0) {
            compare = Long.compare(videoInfo.getDuration(), videoInfo2.getDuration());
        }
        return compare == 0 ? videoInfo.getDisplayName().compareToIgnoreCase(videoInfo2.getDisplayName()) : compare;
    }

    public static int lambda$sortVideoList$4(VideoInfo videoInfo, VideoInfo videoInfo2) {
        int compare = Long.compare(videoInfo2.getDate(), videoInfo.getDate());
        if (compare == 0) {
            compare = Long.compare(videoInfo2.getDuration(), videoInfo.getDuration());
        }
        return compare == 0 ? videoInfo2.getDisplayName().compareToIgnoreCase(videoInfo.getDisplayName()) : compare;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_info_grid, viewGroup, false));
        if (viewType == 1) {
            // Inflate ListView item layout (single row)
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_info_list, viewGroup, false);
            return new MyViewHolder(view);
        } else if (viewType == 2) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_info_grid, viewGroup, false);
            return new MyViewHolder(view);
        }
        return null;

    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        final VideoInfo videoInfo = mVideos.get(i);

        // Set thumbnail and other details
        Glide.with(activity).load(videoInfo.getPath()).centerCrop().into(myViewHolder.ivThumbnail);
        myViewHolder.tvVideoName.setText(videoInfo.getDisplayName());
        myViewHolder.tvCreatedDay.setText(Utility.convertSize(videoInfo.getSize()) + " | " + Utility.convertLongToTime(videoInfo.getDate(), "yyyy-MM-dd"));
        myViewHolder.tvTotalTime.setText(Utility.convertLongToDuration(videoInfo.getDuration()));

        // Ensure mVideoPlaylist is not null
        if (mIsSelectVideoMode && mPlaylist != null) {
            // Show the checkbox and hide the "more" button when in selection mode
            myViewHolder.ivChecked.setVisibility(View.VISIBLE);
            myViewHolder.ivMore.setVisibility(View.GONE);

            // Set the checkbox as checked if the video is in the playlist
            boolean isSelected = mPlaylist.getVideoIdList() != null && mPlaylist.getVideoIdList().contains(videoInfo.getId());
            myViewHolder.ivChecked.setActivated(isSelected);
        } else {
            // Show the "more" button and hide the checkbox
            myViewHolder.ivChecked.setVisibility(View.GONE);
            myViewHolder.ivMore.setVisibility(View.VISIBLE);
        }

        // Handle checkbox click event
        myViewHolder.ivChecked.setOnClickListener(v -> {
            boolean isActivated = myViewHolder.ivChecked.isActivated();
            myViewHolder.ivChecked.setActivated(!isActivated);

            // Update the video playlist in the database if mVideoPlaylist is not null
            if (mPlaylist != null) {
                long dateAdded = mPlaylist.getDateAdded();
                ThreadExecutor.runOnDatabaseThread(() -> m591xa0d6434f(!isActivated, videoInfo, dateAdded));
            }
        });

        // Handle the "more" button click event
        myViewHolder.ivMore.setOnClickListener(v -> m593x5cbd0f0d(i, videoInfo, v));
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public final void onClick(View view) {
                if (!VideoAdapter.this.mIsSelectVideoMode || VideoAdapter.this.mPlaylist == null) {
                    Intent intent = new Intent(VideoAdapter.this.activity, VideoPlayerActivity.class);
                    intent.putExtra(IntentExtra.EXTRA_VIDEO_NUMBER, i);
                    intent.putExtra("path", ((VideoInfo) VideoAdapter.this.mVideos.get(i)).getPath());
                    VideoPlayerActivity.sVideoList = new ArrayList(VideoAdapter.this.mVideos);
                    intent.addFlags(C.ENCODING_PCM_32BIT);
                    activity.startActivity(intent);
                    Log.e("TAG", "m592x7ec9a92e: " + ((VideoInfo) VideoAdapter.this.mVideos.get(0)).getPath());
                    return;
                }
                final boolean isActivated = myViewHolder.ivChecked.isActivated();
                myViewHolder.ivChecked.setActivated(!isActivated); // Toggle the checkbox state
                final long dateAdded = VideoAdapter.this.mPlaylist.getDateAdded();
                ThreadExecutor.runOnDatabaseThread(new Runnable() {
                    public final void run() {
                        VideoAdapter.this.m591xa0d6434f(!isActivated, videoInfo, dateAdded);
                    }
                });

            }
        });

    }

    public void m591xa0d6434f(boolean z, VideoInfo videoInfo, long j) {
        List videoIdList = this.mPlaylist.getVideoIdList();
        if (z) {
            videoIdList.add(Long.valueOf(videoInfo.getId()));
        } else {
            videoIdList.remove(Long.valueOf(videoInfo.getId()));
        }
        MyDatabase.getInstance(this.activity).videoPlaylistDAO().updateVideoListForPlaylist(j, videoIdList);
    }

    public void m593x5cbd0f0d(int i, VideoInfo videoInfo, View view) {
        if (this.mCallback != null) {
            VideoPlayerActivity.sVideoList = new ArrayList(this.mVideos);
            this.mCallback.onMoreClick(i, i, videoInfo);
        }
    }

    private void sortVideoList(List<VideoInfo> list, int i, boolean z) {
        if (i == 0) {
            if (z) {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return VideoAdapter.lambda$sortVideoList$3((VideoInfo) obj, (VideoInfo) obj2);
                    }
                });
            } else {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return VideoAdapter.lambda$sortVideoList$4((VideoInfo) obj, (VideoInfo) obj2);
                    }
                });
            }
        } else if (i == 1) {
            if (z) {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return ((VideoInfo) obj).getDisplayName().compareToIgnoreCase(((VideoInfo) obj2).getDisplayName());
                    }
                });
            } else {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return ((VideoInfo) obj2).getDisplayName().compareToIgnoreCase(((VideoInfo) obj).getDisplayName());
                    }
                });
            }
        } else if (i == 2) {
            if (z) {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return Long.compare(((VideoInfo) obj).getSize(), ((VideoInfo) obj2).getSize());
                    }
                });
            } else {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return Long.compare(((VideoInfo) obj2).getSize(), ((VideoInfo) obj).getSize());
                    }
                });
            }
        } else if (i == 3) {
            if (z) {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return Long.compare(((VideoInfo) obj).getDuration(), ((VideoInfo) obj2).getDuration());
                    }
                });
            } else {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return Long.compare(((VideoInfo) obj2).getDuration(), ((VideoInfo) obj).getDuration());
                    }
                });
            }
        }
    }

    public void sortVideoList(int i, boolean z) {
        if (this.sortMode != i || this.ascending != z) {
            this.sortMode = i;
            this.ascending = z;
            sortVideoList(this.mVideos, i, z);
            notifyDataSetChanged();
        }
    }

    public void setViewMode(int i) {
        if (this.mViewMode != i) {
            this.mViewMode = i;
            notifyDataSetChanged();
        }
    }

    public void updateVideoDataList(List<VideoInfo> list) {
        sortVideoList(list, this.sortMode, this.ascending);
        this.mVideos = new ArrayList(list);
        notifyDataSetChanged();
    }

    public void removeItemPosition(int i) {
        this.mVideos.remove(i);
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, getItemCount());
    }


    public int getItemCount() {
        Log.d("PlayList", "getItemCount: " + mVideos.size());
        return this.mVideos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mViewMode == 2) {
            return 2;
        }
        return 1;
    }


}
