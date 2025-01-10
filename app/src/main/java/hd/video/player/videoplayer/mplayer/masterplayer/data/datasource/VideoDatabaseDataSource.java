package hd.video.player.videoplayer.mplayer.masterplayer.data.datasource;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoHistory;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.VideoFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.util.thread.ThreadExecutor;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class VideoDatabaseDataSource {
    Context mContext;

    public VideoDatabaseDataSource(Context context) {
        this.mContext = context;
    }

    public List<Playlist> getAllPlaylistVideos() {
        ArrayList arrayList = new ArrayList(MyDatabase.getInstance(this.mContext).videoPlaylistDAO().getAllPlaylist());
        Log.d("VideoPlaylist", "Retrieved playlists: " + arrayList.size());
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Playlist playlist = (Playlist) it.next();
            List<Long> videoIdList = playlist.getVideoIdList();
            ArrayList arrayList2 = new ArrayList();
            for (Long l : videoIdList) {
                if (VideoDatabaseControl.getInstance().getVideoById(l.longValue()) != null) {
                    Log.d("VideoPlaylist", "Playlist: " + playlist.getPlaylistName());
                    arrayList2.add(l);
                }
            }
            playlist.setVideoIdList(arrayList2);
        }
        return arrayList;
    }
    public boolean createVideoPlaylist(final Playlist playlist) {
        if (checkPlaylistNameExisted(playlist.getPlaylistName())) {
            return false;
        }
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                VideoDatabaseDataSource.this.m464x35aff0d9(playlist);
            }
        });
        return true;
    }

    public void m464x35aff0d9(Playlist playlist) {
        MyDatabase.getInstance(this.mContext).videoPlaylistDAO().insertNewPlaylist(playlist);
    }

    public boolean duplicateVideoPlaylist(final Playlist playlist) {
        if (checkPlaylistNameExisted(playlist.getPlaylistName())) {
            return false;
        }
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                VideoDatabaseDataSource.this.m468xb14b8245(playlist);
            }
        });
        return true;
    }

    public void m468xb14b8245(Playlist playlist) {
        MyDatabase.getInstance(this.mContext).videoPlaylistDAO().insertNewPlaylist(playlist);
    }

    public boolean updatePlaylistName(final Playlist playlist, final String str) {
        if (checkPlaylistNameExisted(str)) {
            return false;
        }
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                VideoDatabaseDataSource.this.m469xfefdee0a(playlist, str);
            }
        });
        return true;
    }

    public void m469xfefdee0a(Playlist playlist, String str) {
        MyDatabase.getInstance(this.mContext).videoPlaylistDAO().updatePlaylistName(playlist.getDateAdded(), str);
    }

    public void deletePlaylist(final Playlist playlist) {
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                MyDatabase.getInstance(mContext).videoPlaylistDAO().deletePlaylist(playlist.getDateAdded());
            }
        });
    }

    private boolean checkPlaylistNameExisted(String str) {
        String playlistContainingSpecificName = MyDatabase.getInstance(this.mContext).videoPlaylistDAO().getPlaylistContainingSpecificName(str);
        return !TextUtils.isEmpty(playlistContainingSpecificName) && playlistContainingSpecificName.equals(str);
    }
    public List<VideoHistory> getHistoryVideos() {
        ArrayList<VideoHistory> arrayList = new ArrayList<>();
        List<VideoHistory> videoHistories = MyDatabase.getInstance(this.mContext).videoHistoryDAO().getAllHistoryVideo();

        Log.d("VideoHistory", "Video Histories from DB: " + videoHistories.size());

        for (VideoHistory videoHistory : videoHistories) {
            VideoInfo videoById = VideoDatabaseControl.getInstance().getVideoById(videoHistory.getId());

            if (videoById != null) {
                videoHistory.setVideo(videoById);
                arrayList.add(videoHistory);
                Log.d("VideoHistory", "Added Video History ID: " + videoHistory.getId());
            } else {
                Log.d("VideoHistory", "Deleting Video History with ID: " + videoHistory.getId());
                MyDatabase.getInstance(this.mContext).videoHistoryDAO().deleteVideoHistoryById(videoHistory.getId());
            }
        }

        Log.d("VideoHistory", "Final Video History List Size: " + arrayList.size());
        return arrayList;
    }

    public void deleteVideoHistoryById(final long j) {
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                MyDatabase.getInstance(mContext).videoHistoryDAO().deleteVideoHistoryById(j);
            }
        });
    }

    public void deleteAllVideoHistory() {
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                MyDatabase.getInstance(mContext).videoHistoryDAO().deleteAllVideoHistory();
            }
        });
    }
    public void updateVideoHistoryData(final VideoInfo videoInfo) {
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                VideoHistory videoHistory = new VideoHistory();
                videoHistory.setId(videoInfo.getId());
                videoHistory.setVideo(videoInfo);
                videoHistory.setDateAdded(System.currentTimeMillis());
                MyDatabase.getInstance(mContext).videoHistoryDAO().insertNewHistoryVideo(videoHistory);
            }
        });
    }

    public void updateVideoTimeData(long j, long j2) {
        final long j3 = j;
        final long j4 = j2;
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                MyDatabase.getInstance(mContext).videoHistoryDAO().updateVideoTimeData(j, j2);
            }
        });
    }
    public int getAVideoTimeData(long j) {
        int aVideoTimeData = MyDatabase.getInstance(this.mContext).videoHistoryDAO().getAVideoTimeData(j);
        return aVideoTimeData;
    }

    public List<VideoInfo> getAllFavoriteVideo() {
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        for (Long l : VideoFavoriteUtil.getAllFavoriteVideoId(this.mContext)) {
            VideoInfo videoById = VideoDatabaseControl.getInstance().getVideoById(l.longValue());
            if (videoById != null) {
                arrayList.add(videoById);
                hashSet.add(l);
            }
        }
        VideoFavoriteUtil.setFavoriteVideoId(this.mContext, hashSet);
        return arrayList;
    }

    public List<VideoInfo> getAllVideoOfPlaylist(Playlist playlist) {
        ArrayList arrayList = new ArrayList();
        Iterator it = new HashSet(MyDatabase.getInstance(this.mContext).videoPlaylistDAO().getPlaylistByDateAdded(playlist.getDateAdded()).getVideoIdList()).iterator();
        while (it.hasNext()) {
            VideoInfo videoById = VideoDatabaseControl.getInstance().getVideoById(((Long) it.next()).longValue());
            if (videoById != null) {
                arrayList.add(videoById);
            }
        }
        return arrayList;
    }

    public List<VideoInfo> searchVideoByVideoName(String str) {
        if (TextUtils.isEmpty(str.trim())) {
            return new ArrayList(VideoDatabaseControl.getInstance().getAllVideos());
        }
        return VideoDatabaseControl.getInstance().searchVideoByVideoName(str);
    }

    public List<VideoInfo> getAllVideoHidden() throws IOException {
        ArrayList arrayList = new ArrayList();
        File file = new File(Utility.sHiddenVideoPath);
        if (!file.exists()) {
            file.mkdir();
        }
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            for (File file2 : listFiles) {
                String name = file2.getName();
                if (!file2.isDirectory() && (name.endsWith(".mp4") || name.endsWith(".flv") || name.endsWith(".avi") || name.endsWith(".mkv") || name.endsWith(".mov") || name.endsWith(".3gp") || name.endsWith(".m4v") || name.endsWith(".webm") || name.endsWith(".qt") || name.endsWith(".wmv"))) {
                    VideoInfo videoInfo = new VideoInfo();
                    mediaMetadataRetriever.setDataSource(file2.getPath());
                    videoInfo.setDuration(Long.parseLong(mediaMetadataRetriever.extractMetadata(9)));
                    videoInfo.setDisplayName(file2.getName());
                    videoInfo.setSize(file2.length());
                    videoInfo.setPath(file2.getPath());
                    videoInfo.setUri(Uri.fromFile(file2).toString());
                    arrayList.add(videoInfo);
                }
            }
            mediaMetadataRetriever.release();
        }
        Collections.sort(arrayList, new Comparator() {
            public final int compare(Object obj, Object obj2) {
                return ((VideoInfo) obj2).getDisplayName().compareToIgnoreCase(((VideoInfo) obj).getDisplayName());
            }
        });
        return arrayList;
    }
}
