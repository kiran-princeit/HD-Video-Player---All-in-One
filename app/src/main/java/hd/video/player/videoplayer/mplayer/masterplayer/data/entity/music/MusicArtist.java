package hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music;

import java.util.ArrayList;
import java.util.List;

public class MusicArtist {
    private long artistId;
    private String artistName;
    private List<MusicInfo> mMusicList;

    public long getArtistId() {
        return this.artistId;
    }

    public void setArtistId(long j) {
        this.artistId = j;
    }

    public String getArtistName() {
        String str = this.artistName;
        return str == null ? "" : str;
    }

    public void setArtistName(String str) {
        this.artistName = str;
    }

    public List<MusicInfo> getMusicList() {
        List<MusicInfo> list = this.mMusicList;
        return list == null ? new ArrayList() : list;
    }

    public void setMusicList(List<MusicInfo> list) {
        this.mMusicList = list;
    }
}
