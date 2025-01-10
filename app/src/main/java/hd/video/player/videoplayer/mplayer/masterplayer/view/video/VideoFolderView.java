package hd.video.player.videoplayer.mplayer.masterplayer.view.video;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoFolder;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface VideoFolderView extends BaseView {
    void updateFolderList(List<VideoFolder> list);
}
