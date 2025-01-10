package hd.video.player.videoplayer.mplayer.masterplayer.view.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicAlbum;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface MusicAlbumView extends BaseView {
    void onUpdateAlbum(List<MusicAlbum> list);

    void onUpdateMusicList(List<MusicInfo> list);
}
