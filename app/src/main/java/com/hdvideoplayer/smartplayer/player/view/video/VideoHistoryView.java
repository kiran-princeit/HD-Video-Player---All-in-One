package com.hdvideoplayer.smartplayer.player.view.video;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoHistory;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface VideoHistoryView extends BaseView {
    void updateHistoryVideos(List<VideoHistory> list);
}
