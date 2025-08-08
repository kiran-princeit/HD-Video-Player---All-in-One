package com.hdvideoplayer.smartplayer.player.adapters.video;

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
import com.bumptech.glide.RequestBuilder;
import com.google.android.exoplayer2.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.activities.VideoPlayerActivity;
import com.hdvideoplayer.smartplayer.player.data.database.MyDatabase;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.video.Playlist;
import com.hdvideoplayer.smartplayer.player.util.Utility;
import com.hdvideoplayer.smartplayer.player.util.thread.ThreadExecutor;
import com.hdvideoplayer.smartplayer.player.util.constant.AppConstant;

public class VideoInfoAdapter extends Adapter<VideoInfoAdapter.ViewHolder> {
    private final Activity activity;
    private boolean ascending;
    private final Callback mCallback;
    private boolean mIsSelectVideoMode;
    private final Playlist mPlaylist;
    private List<VideoInfo> mVideos = new ArrayList();
    private int mViewMode = 1;
    private int sortMode;

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    public interface Callback {
        void onMoreClick(int i, int i2, VideoInfo videoInfo);
    }

    public class MyViewHolder extends ViewHolder {
        private ImageView ivChecked;
        private ImageView ivMore;
        private ImageView ivThumbnail,ivFolderIcon;
        private TextView tvCreatedDay;
        private TextView tvTotalTime;
        private TextView tvVideoName,tvFolderName;

        public MyViewHolder(View view) {
            super(view);
            this.ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            this.tvVideoName = (TextView) view.findViewById(R.id.tv_video_name);
            this.tvCreatedDay = (TextView) view.findViewById(R.id.tv_created_day);
            this.ivMore = (ImageView) view.findViewById(R.id.iv_more);
            this.tvTotalTime = (TextView) view.findViewById(R.id.tv_total_time);
            this.ivChecked = (ImageView) view.findViewById(R.id.iv_checked);
            this.tvFolderName = (TextView) view.findViewById(R.id.tvFolderName);
            this.ivFolderIcon = (ImageView) view.findViewById(R.id.ivFolderIcon);
            this.tvCreatedDay.setSelected(true);
            this.tvVideoName.setSelected(true);
            this.tvTotalTime.setSelected(true);
        }
    }

    public VideoInfoAdapter(Activity activity, boolean z, Callback callback, Playlist playlist) {
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
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


//    public int getItemViewType(int i) {
//        i++;
//        return (i % 5 != 0 || i == 1) ? 1 : 2;
//    }
//
    @Override
    public int getItemViewType(int position) {
        if (mViewMode == 2) {
            return 2;
        }
        return 1;
    }


    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
//        Log.d("aaa", "position = " + i);
//        if (viewHolder.getItemViewType() == 1) {
        final MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        final VideoInfo videoInfo = (VideoInfo) this.mVideos.get(i);
        ((RequestBuilder) Glide.with(this.activity).load(videoInfo.getPath()).centerCrop()).into(myViewHolder.ivThumbnail);
        myViewHolder.tvVideoName.setText(videoInfo.getDisplayName());
        myViewHolder.tvCreatedDay.setText(Utility.convertSize(videoInfo.getSize()) + " | " + Utility.convertLongToTime(videoInfo.getDate(), "yyyy-MM-dd"));
        myViewHolder.tvTotalTime.setText(Utility.convertLongToDuration(videoInfo.getDuration()));

        String folder = videoInfo.getFolder();
        if (folder != null && !folder.isEmpty()) {
            String folderName = folder.substring(folder.lastIndexOf("/") + 1);
            if (folderName.contains("Wa") || folderName.contains("hats")) {
                Glide.with(this.activity).load(R.drawable.small_folder).into(myViewHolder.ivFolderIcon);
            } else if (folderName.contains("own")) {
                Glide.with(this.activity).load(R.drawable.small_folder).into(myViewHolder.ivFolderIcon);
            } else if (folderName.contains("amera")) {
                Glide.with(this.activity).load(R.drawable.small_folder).into(myViewHolder.ivFolderIcon);
            } else {
                Glide.with(this.activity).load(R.drawable.small_folder).into(myViewHolder.ivFolderIcon);
            }
            String capitalizedFolderName = folderName.substring(0, 1).toUpperCase() + folderName.substring(1);
            myViewHolder.tvFolderName.setText(capitalizedFolderName);
        } else {
            myViewHolder.tvFolderName.setText("Unknown Folder");
            Glide.with(this.activity).load(R.drawable.small_folder).into(myViewHolder.ivFolderIcon);
        }



        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public final void onClick(View view) {
                if (!VideoInfoAdapter.this.mIsSelectVideoMode || VideoInfoAdapter.this.mPlaylist == null) {
                    Intent intent = new Intent(VideoInfoAdapter.this.activity, VideoPlayerActivity.class);
                    intent.putExtra(AppConstant.IntentExtra.EXTRA_VIDEO_NUMBER, i);
                    intent.putExtra("path", ((VideoInfo) VideoInfoAdapter.this.mVideos.get(i)).getPath());
                    VideoPlayerActivity.sVideoList = new ArrayList(VideoInfoAdapter.this.mVideos);
                    intent.addFlags(C.ENCODING_PCM_32BIT);
                    activity.startActivity(intent);
                    Log.e("TAG", "aaaaaaaaaaaa: " + ((VideoInfo) VideoInfoAdapter.this.mVideos.get(0)).getPath());
                    return;
                }
                final boolean isActivated = myViewHolder.ivChecked.isActivated();
                myViewHolder.ivChecked.setActivated(isActivated);
                final long dateAdded = VideoInfoAdapter.this.mPlaylist.getDateAdded();
                ThreadExecutor.runOnDatabaseThread(new Runnable() {
                    public final void run() {
                        VideoInfoAdapter.this.chechBoxClick(isActivated, videoInfo, dateAdded);
                    }
                });
            }
        });
        if (!this.mIsSelectVideoMode || this.mPlaylist == null) {
            myViewHolder.ivChecked.setVisibility(8);
            myViewHolder.ivMore.setVisibility(0);
            myViewHolder.ivMore.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    VideoInfoAdapter.this.moreItemClick(i, videoInfo, view);
                }
            });
            return;
        }
        myViewHolder.ivMore.setVisibility(8);
        myViewHolder.ivChecked.setVisibility(0);
        myViewHolder.ivChecked.setActivated(this.mPlaylist.getVideoIdList().contains(Long.valueOf(videoInfo.getId())));
//        }


    }

    public void chechBoxClick(boolean z, VideoInfo videoInfo, long j) {
        List videoIdList = this.mPlaylist.getVideoIdList();
        if (z) {
            videoIdList.add(Long.valueOf(videoInfo.getId()));
        } else {
            videoIdList.remove(Long.valueOf(videoInfo.getId()));
        }
        MyDatabase.getInstance(this.activity).videoPlaylistDAO().updateVideoListForPlaylist(j, videoIdList);
    }

    public void moreItemClick(int i, VideoInfo videoInfo, View view) {
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
                        return VideoInfoAdapter.lambda$sortVideoList$3((VideoInfo) obj, (VideoInfo) obj2);
                    }
                });
            } else {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return VideoInfoAdapter.lambda$sortVideoList$4((VideoInfo) obj, (VideoInfo) obj2);
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
        return this.mVideos.size();
    }
}
