package hd.video.player.videoplayer.mplayer.masterplayer.view.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface MusicSearchView extends BaseView {
    void onSearchMusic(List<MusicInfo> list);
}
