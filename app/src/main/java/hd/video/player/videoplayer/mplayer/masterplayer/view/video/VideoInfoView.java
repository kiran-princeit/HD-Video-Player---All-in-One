package hd.video.player.videoplayer.mplayer.masterplayer.view.video;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface VideoInfoView extends BaseView {
    void updateVideoList(List<VideoInfo> list);
}
