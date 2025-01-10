package hd.video.player.videoplayer.mplayer.masterplayer.view.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface MusicPlaylistView extends BaseView {
    void onCreatePlaylist(boolean z, MusicPlaylist musicPlaylist);

    void onDuplicationPlaylist(MusicPlaylist musicPlaylist);

    void onUpdatePlaylistName(int i, String str, boolean z);

    void updateMusicPlaylist(List<MusicPlaylist> list);
}
