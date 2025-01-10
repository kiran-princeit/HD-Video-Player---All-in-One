package hd.video.player.videoplayer.mplayer.masterplayer.presenter.video;

import java.util.ArrayList;
import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.VideoDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.video.PlaylistView;

public class PlaylistPresenter extends BasePresenter<PlaylistView> {
    private final VideoDataRepository mVideoRepository;

    public PlaylistPresenter(PlaylistView playlistView, VideoDataRepository videoDataRepository) {
        super(playlistView);
        this.mVideoRepository = videoDataRepository;
    }

    public void openPlaylistTab() {
        this.mVideoRepository.fetchAllPlaylistVideos(new ILoaderRepository.LoadDataListener<Playlist>() {
            public void onError() {
            }

            public void onSuccess(List<Playlist> list) {
                if (PlaylistPresenter.this.mView != null) {
                    ((PlaylistView) PlaylistPresenter.this.mView).updateVideoPlaylist(list);
                }
            }
        });
    }

    public void createNewPlaylist(String str) {
        final Playlist playlist = new Playlist();
        playlist.setPlaylistName(str.trim());
        playlist.setVideoIdList(new ArrayList());
        playlist.setDateAdded(System.currentTimeMillis());
        this.mVideoRepository.createNewPlaylist(new ILoaderRepository.InsertDataListener() {
            public void onSuccess() {
                if (PlaylistPresenter.this.mView != null) {
                    ((PlaylistView) PlaylistPresenter.this.mView).onCreatePlaylist(true, playlist);
                }
            }

            public void onError() {
                if (PlaylistPresenter.this.mView != null) {
                    ((PlaylistView) PlaylistPresenter.this.mView).onCreatePlaylist(false, playlist);
                }
            }
        }, playlist);
    }

    public void deletePlaylist(Playlist playlist) {
        this.mVideoRepository.deletePlaylist(playlist);
    }

    public void duplicateVideoPlaylist(String str, Playlist playlist) {
        final Playlist playlist2 = new Playlist(str);
        playlist2.setDateAdded(System.currentTimeMillis());
        playlist2.setVideoIdList(playlist.getVideoIdList());
        this.mVideoRepository.duplicatePlaylist(new ILoaderRepository.InsertDataListener() {
            public void onSuccess() {
                if (PlaylistPresenter.this.mView != null) {
                    ((PlaylistView) PlaylistPresenter.this.mView).onDuplicationPlaylist(playlist2);
                }
            }

            public void onError() {
                if (PlaylistPresenter.this.mView != null) {
                    ((PlaylistView) PlaylistPresenter.this.mView).onDuplicationPlaylist((Playlist) null);
                }
            }
        }, playlist2);
    }

    public void updatePlaylistName(Playlist playlist, final String str, final int i) {
        this.mVideoRepository.updatePlaylistName(new ILoaderRepository.InsertDataListener() {
            public void onSuccess() {
                if (PlaylistPresenter.this.mView != null) {
                    ((PlaylistView) PlaylistPresenter.this.mView).onUpdatePlaylistName(i, str, true);
                }
            }

            public void onError() {
                if (PlaylistPresenter.this.mView != null) {
                    ((PlaylistView) PlaylistPresenter.this.mView).onUpdatePlaylistName(i, str, false);
                }
            }
        }, playlist, str);
    }
}
