package hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video;

import android.os.Parcel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.MultiItemEntity;

public class Playlist implements Serializable, MultiItemEntity, hd.video.player.videoplayer.mplayer.masterplayer.base.entity.MultiItemEntity {
    public static int viewMode = 1;
    private long mDateAdded;
    private String mPlaylistName;
    private List<Long> mVideoIdList;

    public Playlist() {
    }

    public Playlist(String str) {
        this.mPlaylistName = str;
    }

    protected Playlist(Parcel parcel) {
        this.mPlaylistName = parcel.readString();
        this.mDateAdded = parcel.readLong();
    }

    public long getDateAdded() {
        return this.mDateAdded;
    }

    public void setDateAdded(long j) {
        this.mDateAdded = j;
    }

    public String getPlaylistName() {
        String str = this.mPlaylistName;
        return str == null ? "" : str;
    }

    public void setPlaylistName(String str) {
        this.mPlaylistName = str;
    }

    public List<Long> getVideoIdList() {
        List<Long> list = this.mVideoIdList;
        return list == null ? new ArrayList() : list;
    }

    public void setVideoIdList(List<Long> list) {
        this.mVideoIdList = list;
    }

    public int getItemType() {
        return viewMode;
    }
}
