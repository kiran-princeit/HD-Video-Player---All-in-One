package hd.video.player.videoplayer.mplayer.masterplayer.view.video;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoHistory;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface VideoHistoryView extends BaseView {
    void updateHistoryVideos(List<VideoHistory> list);
}
