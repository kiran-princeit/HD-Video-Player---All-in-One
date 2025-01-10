package hd.video.player.videoplayer.mplayer.masterplayer.data.dao.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;

public abstract class MusicPlaylistDAO {
    public abstract void deletePlaylist(long j);

    public abstract List<MusicPlaylist> getAllPlaylist();

    public abstract MusicPlaylist getPlaylistByDateAdded(long j);

    public abstract String getPlaylistContainingSpecificName(String str);

    public abstract void insertNewPlaylist(MusicPlaylist musicPlaylist);

    public abstract int updateVideoPlaylist(MusicPlaylist musicPlaylist);
    public void updateMusicListForPlaylist(long j, List<Long> list) {
        MusicPlaylist playlistByDateAdded = getPlaylistByDateAdded(j);

        if (playlistByDateAdded == null) {
            playlistByDateAdded = new MusicPlaylist(); // Create a new playlist
        }

        playlistByDateAdded.setMusicIdList(list);
        updateVideoPlaylist(playlistByDateAdded);
    }


    public void updatePlaylistName(long j, String str) {
        MusicPlaylist playlistByDateAdded = getPlaylistByDateAdded(j);
        playlistByDateAdded.setPlaylistName(str);
        updateVideoPlaylist(playlistByDateAdded);
    }
}
