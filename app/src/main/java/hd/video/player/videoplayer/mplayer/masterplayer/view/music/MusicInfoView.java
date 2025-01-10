package hd.video.player.videoplayer.mplayer.masterplayer.view.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface MusicInfoView extends BaseView {
    void updateMusicList(List<MusicInfo> list);
}
