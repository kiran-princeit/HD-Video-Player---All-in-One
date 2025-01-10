package hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music;

public class MusicHistory {
    private long mCurrentPosition;
    private long mDateAdded;
    private long mId;
    private MusicInfo musics;

    public long getId() {
        return this.mId;
    }

    public void setId(long j) {
        this.mId = j;
    }

    public long getDateAdded() {
        return this.mDateAdded;
    }

    public void setDateAdded(long j) {
        this.mDateAdded = j;
    }

    public MusicInfo getMusics() {
        return this.musics;
    }

    public void setMusic(MusicInfo musicInfo) {
        this.musics = musicInfo;
    }

    public long getCurrentPosition() {
        return this.mCurrentPosition;
    }

    public void setCurrentPosition(long j) {
        this.mCurrentPosition = j;
    }
}
