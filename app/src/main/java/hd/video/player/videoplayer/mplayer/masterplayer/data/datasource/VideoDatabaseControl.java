package hd.video.player.videoplayer.mplayer.masterplayer.data.datasource;

import android.os.Build;
import android.util.Log;
import android.util.LongSparseArray;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;

public class VideoDatabaseControl {
    private static volatile VideoDatabaseControl sInstance;
    private List<VideoInfo> mRecentlyVideo;
    private Set<VideoInfo> mSetVideos = new HashSet();
    private LongSparseArray<VideoInfo> mSparseVideos = new LongSparseArray();

    private VideoDatabaseControl() {
    }

    public static VideoDatabaseControl getInstance() {
        if (sInstance == null) {
            synchronized (VideoDatabaseControl.class) {
                if (sInstance == null) {
                    sInstance = new VideoDatabaseControl();
                }
            }
        }
        return sInstance;
    }
    public void addVideo(VideoInfo videoInfo) {
        this.mSparseVideos.put(videoInfo.getId(), videoInfo);
        this.mSetVideos.add(videoInfo);
        this.mRecentlyVideo = null;
    }

    public void setNewData(Set<VideoInfo> set, LongSparseArray<VideoInfo> longSparseArray) {
        this.mSparseVideos = longSparseArray;
        this.mSetVideos = set;
        this.mRecentlyVideo = null;
    }

    public Set<VideoInfo> getAllVideos() {
        return this.mSetVideos;
    }

    public VideoInfo getVideoById(long j) {
        return (VideoInfo) this.mSparseVideos.get(j);
    }

    public void removeVideoById(long j) {
        this.mSetVideos.remove(this.mSparseVideos.get(j));
        this.mSparseVideos.remove(j);
        this.mRecentlyVideo = null;
    }

    public List<VideoInfo> searchVideoByVideoName(final String str) {
        ArrayList arrayList = new ArrayList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return (List) this.mSetVideos.stream().filter(new Predicate() {
                public final boolean test(Object obj) {
                    return ((VideoInfo) obj).getDisplayName().toLowerCase().contains(str.trim().toLowerCase());
                }
            }).collect(Collectors.toList());
        }
        return arrayList;
    }

    public List<VideoInfo> getAllRecentlyVideo() {
        if (mRecentlyVideo != null) return mRecentlyVideo;

        mRecentlyVideo = new ArrayList<>();
        if (mSetVideos == null || mSetVideos.isEmpty()) return mRecentlyVideo;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mRecentlyVideo = mSetVideos.stream()
                        .filter(videoInfo -> videoInfo.getDate() > System.currentTimeMillis() - 604800000)
                        .collect(Collectors.toList());
            } else {
                for (VideoInfo videoInfo : mSetVideos) {
                    if (videoInfo.getDate() > System.currentTimeMillis() - 604800000) {
                        this.mRecentlyVideo.add(videoInfo);
                        Log.d("VideoFilter", "Added video: " + videoInfo.getDisplayName());
                    } else {
                        Log.d("VideoFilter", "Skipped video: " + videoInfo.getDisplayName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mRecentlyVideo.clear();
        }
        return mRecentlyVideo;
    }
}
