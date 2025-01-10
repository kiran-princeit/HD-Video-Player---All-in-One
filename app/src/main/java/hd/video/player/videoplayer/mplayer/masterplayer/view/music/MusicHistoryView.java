package hd.video.player.videoplayer.mplayer.masterplayer.view.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicHistory;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface MusicHistoryView extends BaseView {
    void updateMusicHistoryList(List<MusicHistory> list);
}
