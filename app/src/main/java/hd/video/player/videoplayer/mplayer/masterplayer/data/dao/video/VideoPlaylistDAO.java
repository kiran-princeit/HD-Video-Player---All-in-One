package hd.video.player.videoplayer.mplayer.masterplayer.data.dao.video;

import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;

public abstract class VideoPlaylistDAO {
    public abstract void deletePlaylist(long j);

    public abstract List<Playlist> getAllPlaylist();

    public abstract Playlist getPlaylistByDateAdded(long j);

    public abstract String getPlaylistContainingSpecificName(String str);

    public abstract void insertNewPlaylist(Playlist playlist);

    public abstract int updateVideoPlaylist(Playlist playlist);

    public void updateVideoListForPlaylist(long j, List<Long> list) {
        Playlist playlistByDateAdded = getPlaylistByDateAdded(j);

        if (playlistByDateAdded == null) {
            playlistByDateAdded = new Playlist(); // Create a new playlist
        }

        playlistByDateAdded.setVideoIdList(list);
        updateVideoPlaylist(playlistByDateAdded);
    }


    public void updatePlaylistName(long j, String str) {
        Playlist playlistByDateAdded = getPlaylistByDateAdded(j);
        playlistByDateAdded.setPlaylistName(str);
        updateVideoPlaylist(playlistByDateAdded);
    }
}



