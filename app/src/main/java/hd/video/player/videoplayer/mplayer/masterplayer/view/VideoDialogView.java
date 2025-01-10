package hd.video.player.videoplayer.mplayer.masterplayer.view;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;

public interface VideoDialogView extends BaseView {
    void onVideoLoader(List<VideoInfo> list);
}
