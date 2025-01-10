package hd.video.player.videoplayer.mplayer.masterplayer.view.video;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface PlaylistView extends BaseView {
    void onCreatePlaylist(boolean z, Playlist playlist);

    void onDuplicationPlaylist(Playlist playlist);

    void onUpdatePlaylistName(int i, String str, boolean z);

    void updateVideoPlaylist(List<Playlist> list);
}
