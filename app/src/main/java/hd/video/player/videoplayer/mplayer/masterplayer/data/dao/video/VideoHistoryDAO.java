package hd.video.player.videoplayer.mplayer.masterplayer.data.dao.video;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoHistory;

public abstract class VideoHistoryDAO {
    public abstract void deleteAllVideoHistory();

    public abstract void deleteVideoHistoryById(long j);

    public abstract VideoHistory getAHistoryVideo(long j);

    public abstract int getAVideoTimeData(long j);

    public abstract List<VideoHistory> getAllHistoryVideo();

    public abstract void insertNewHistoryVideo(VideoHistory videoHistory);

    public abstract int updateHistoryVideo(VideoHistory videoHistory);

    public void updateVideoTimeData(long j, long j2) {
        VideoHistory aHistoryVideo = getAHistoryVideo(j);
        if (aHistoryVideo != null) {
            aHistoryVideo.setCurrentPosition(j2);
            updateHistoryVideo(aHistoryVideo);
        }
    }
}
