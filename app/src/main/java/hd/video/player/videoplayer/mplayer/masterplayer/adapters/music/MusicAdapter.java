package hd.video.player.videoplayer.mplayer.masterplayer.adapters.music;

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

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.activities.PlayMusicActivity;
import hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.AdManager;
import hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.OnActivityResultLauncher1;
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;

import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.util.music.MusicPlayerUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.thread.ThreadExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant;

public class MusicAdapter extends Adapter<MusicAdapter.ViewHolder> {
    public static final int AD_TYPE = 2;
    public static final int CONTENT_TYPE = 1;
    private final Activity activity;
    private boolean ascending;
    private final Callback mCallback;
    private boolean mIsSelectMusicMode;
    private final MusicPlaylist mMusicPlaylist;
    private List<MusicInfo> mMusics = new ArrayList();
    private int mViewMode = 1;
    private int sortMode;

    public interface Callback {
        void onMoreClick(int i, int i2, MusicInfo MusicInfo);
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
        private TextView tvArtist;
        private TextView tvDuration;
        private TextView tvSong;


        public MyViewHolder(View view) {
            super(view);
            this.ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            this.tvSong = (TextView) view.findViewById(R.id.tv_song);
            this.tvArtist = (TextView) view.findViewById(R.id.tv_artist);
            this.tvDuration = (TextView) view.findViewById(R.id.tv_duration);
            this.ivMore = (ImageView) view.findViewById(R.id.iv_more);
            this.ivChecked = (ImageView) view.findViewById(R.id.iv_checked);

        }
    }

    public int getItemViewType(int i) {
        return i;
    }

    public MusicAdapter(Activity activity, boolean z, Callback callback, MusicPlaylist MusicPlaylist) {
        this.activity = activity;
        this.mMusicPlaylist = MusicPlaylist;
        this.mIsSelectMusicMode = z;
        this.mCallback = callback;
        this.sortMode = Utility.getVideoSortMode(activity);
        this.ascending = Utility.getVideoSortAscending(activity);
    }

    public static int lambda$sortVideoList$3(MusicInfo MusicInfo, MusicInfo MusicInfo2) {
        int compare = Long.compare(MusicInfo.getDate(), MusicInfo2.getDate());
        if (compare == 0) {
            compare = Long.compare(MusicInfo.getDuration(), MusicInfo2.getDuration());
        }
        return compare == 0 ? MusicInfo.getDisplayName().compareToIgnoreCase(MusicInfo2.getDisplayName()) : compare;
    }

    public static int lambda$sortVideoList$4(MusicInfo MusicInfo, MusicInfo MusicInfo2) {
        int compare = Long.compare(MusicInfo2.getDate(), MusicInfo.getDate());
        if (compare == 0) {
            compare = Long.compare(MusicInfo2.getDuration(), MusicInfo.getDuration());
        }
        return compare == 0 ? MusicInfo2.getDisplayName().compareToIgnoreCase(MusicInfo.getDisplayName()) : compare;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_info_list, viewGroup, false));
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music_info, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
        final MusicInfo musicInfo = mMusics.get(i);


        myViewHolder.tvSong.setText(musicInfo.getDisplayName());
        myViewHolder.tvArtist.setText(musicInfo.getArtist());
        myViewHolder.tvDuration.setText(Utility.convertLongToDuration(musicInfo.getDuration()));
        ((RequestBuilder) ((RequestBuilder) Glide.with(this.activity).load(MusicPlayerUtils.getThumbnailOfSong(this.activity, musicInfo.getPath(), 60)).centerCrop()).error(R.drawable.ic_music_icon)).into(myViewHolder.ivThumbnail);


        if (mIsSelectMusicMode && mMusicPlaylist != null) {
            myViewHolder.ivChecked.setVisibility(View.VISIBLE);
            myViewHolder.ivMore.setVisibility(View.GONE);

            boolean isSelected = mMusicPlaylist.getMusicIdList() != null && mMusicPlaylist.getMusicIdList().contains(musicInfo.getId());
            myViewHolder.ivChecked.setActivated(isSelected);
        } else {
            myViewHolder.ivChecked.setVisibility(View.GONE);
            myViewHolder.ivMore.setVisibility(View.VISIBLE);
        }


        myViewHolder.ivChecked.setOnClickListener(v -> {
            boolean isActivated = myViewHolder.ivChecked.isActivated();
            myViewHolder.ivChecked.setActivated(!isActivated);

            if (mMusicPlaylist != null) {
                long dateAdded = mMusicPlaylist.getDateAdded();
                ThreadExecutor.runOnDatabaseThread(() -> getmusicSelected(!isActivated, musicInfo, dateAdded));
            }
        });


        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public final void onClick(View view) {
                if (!MusicAdapter.this.mIsSelectMusicMode || MusicAdapter.this.mMusicPlaylist == null) {
                    AdManager.showInterstitial(activity, new OnActivityResultLauncher1.OnActivityResultLauncher2() {
                        @Override
                        public void onLauncher() {
                            // Create an Intent to start PlayMusicActivity
                            Intent intent = new Intent(activity, PlayMusicActivity.class);
                            intent.putExtra(AppConstant.IntentExtra.EXTRA_MUSIC_NUMBER, i);
                            intent.putExtra(AppConstant.IntentExtra.EXTRA_MUSIC_ARRAY, (ArrayList) getAllMusicId());
                            // It's better to check if this flag is appropriate for the context
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);

                        }
                    });
                } else {
                    // Toggle the selection state of the item (checked or unchecked).
                    final boolean isActivated = myViewHolder.ivChecked.isActivated();
                    myViewHolder.ivChecked.setActivated(!isActivated); // Toggling the activation state
                    final long dateAdded = mMusicPlaylist.getDateAdded(); // Get the playlist's date added
                    ThreadExecutor.runOnDatabaseThread(new Runnable() {
                        public final void run() {
                            getmusicSelected(!isActivated, musicInfo, dateAdded); // Adding/removing the music
                        }
                    });


                }
            }
        });

    }

    private List<Long> getAllMusicId() {
        ArrayList arrayList = new ArrayList();
        List<MusicInfo> list = this.mMusics;
        if (list == null) {
            return new ArrayList();
        }
        for (MusicInfo id : list) {
            arrayList.add(Long.valueOf(id.getId()));
        }
        return arrayList;
    }


    public void getmusicSelected(boolean z, MusicInfo MusicInfo, long j) {
        List videoIdList = this.mMusicPlaylist.getMusicIdList();
        if (z) {
            videoIdList.add(Long.valueOf(MusicInfo.getId()));
        } else {
            videoIdList.remove(Long.valueOf(MusicInfo.getId()));
        }
        MyDatabase.getInstance(this.activity).musicPlaylistDAO().updateMusicListForPlaylist(j, videoIdList);
    }

    private void sortVideoList(List<MusicInfo> list, int i, boolean z) {
        if (i == 0) {
            if (z) {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return MusicAdapter.lambda$sortVideoList$3((MusicInfo) obj, (MusicInfo) obj2);
                    }
                });
            } else {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return MusicAdapter.lambda$sortVideoList$4((MusicInfo) obj, (MusicInfo) obj2);
                    }
                });
            }
        } else if (i == 1) {
            if (z) {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return ((MusicInfo) obj).getDisplayName().compareToIgnoreCase(((MusicInfo) obj2).getDisplayName());
                    }
                });
            } else {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return ((MusicInfo) obj2).getDisplayName().compareToIgnoreCase(((MusicInfo) obj).getDisplayName());
                    }
                });
            }
        } else if (i == 2) {
            if (z) {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return Long.compare(((MusicInfo) obj).getSize(), ((MusicInfo) obj2).getSize());
                    }
                });
            } else {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return Long.compare(((MusicInfo) obj2).getSize(), ((MusicInfo) obj).getSize());
                    }
                });
            }
        } else if (i == 3) {
            if (z) {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return Long.compare(((MusicInfo) obj).getDuration(), ((MusicInfo) obj2).getDuration());
                    }
                });
            } else {
                Collections.sort(list, new Comparator() {
                    public final int compare(Object obj, Object obj2) {
                        return Long.compare(((MusicInfo) obj2).getDuration(), ((MusicInfo) obj).getDuration());
                    }
                });
            }
        }
    }

    public void sortMusicList(int i, boolean z) {
        if (this.sortMode != i || this.ascending != z) {
            this.sortMode = i;
            this.ascending = z;
            sortVideoList(this.mMusics, i, z);
            notifyDataSetChanged();
        }
    }

    public void updateMusicDataList(List<MusicInfo> list) {
        sortVideoList(list, this.sortMode, this.ascending);
        this.mMusics = new ArrayList(list);
        notifyDataSetChanged();
    }

    public void removeItemPosition(int i) {
        this.mMusics.remove(i);
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, getItemCount());
    }

    public int getItemCount() {

        Log.d("PlayList", "getItemCount: " + mMusics.size());
        return this.mMusics.size();
    }
}
