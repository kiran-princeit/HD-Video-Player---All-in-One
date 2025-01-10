package hd.video.player.videoplayer.mplayer.masterplayer.view;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;

public interface MusicDialogView extends BaseView {
    void onMusicLoader(List<MusicInfo> list);
}
