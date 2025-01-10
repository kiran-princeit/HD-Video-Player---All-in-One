package hd.video.player.videoplayer.mplayer.masterplayer.presenter.music;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.InsertDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicPlaylistView;

public class MusicPlaylistPresenter extends BasePresenter<MusicPlaylistView> {
    private Context mContext;
    private MusicDataRepository mMusicRepository;

    public MusicPlaylistPresenter(Context context, MusicPlaylistView musicPlaylistView, MusicDataRepository musicDataRepository) {
        super(musicPlaylistView);
        this.mMusicRepository = musicDataRepository;
        this.mContext = context;
    }

    public void loadPlaylistTab() {
        this.mMusicRepository.fetchAllPlaylists(new LoadDataListener<MusicPlaylist>() {
            public void onError() {
            }

            public void onSuccess(List<MusicPlaylist> list) {
                if (MusicPlaylistPresenter.this.mView != null) {
                    ((MusicPlaylistView) MusicPlaylistPresenter.this.mView).updateMusicPlaylist(list);
                }
            }
        });
    }

    public void createMusicPlaylist(String str) {
        final MusicPlaylist musicPlaylist = new MusicPlaylist(str);
        musicPlaylist.setPlaylistName(str.trim());
        musicPlaylist.setMusicIdList(new ArrayList());
        musicPlaylist.setDateAdded(System.currentTimeMillis());
        this.mMusicRepository.createPlaylist(new InsertDataListener() {
            public void onSuccess() {
                if (MusicPlaylistPresenter.this.mView != null) {
                    ((MusicPlaylistView) MusicPlaylistPresenter.this.mView).onCreatePlaylist(true, musicPlaylist);
                }
            }

            public void onError() {
                if (MusicPlaylistPresenter.this.mView != null) {
                    ((MusicPlaylistView) MusicPlaylistPresenter.this.mView).onCreatePlaylist(false, musicPlaylist);
                }
            }
        }, musicPlaylist);
    }

    public void deletePlaylist(MusicPlaylist musicPlaylist) {
        this.mMusicRepository.deletePlaylist(musicPlaylist);
    }

    public void duplicateMusicPlaylist(String str, MusicPlaylist musicPlaylist) {
        final MusicPlaylist musicPlaylist2 = new MusicPlaylist(str);
        musicPlaylist2.setDateAdded(System.currentTimeMillis());
        musicPlaylist2.setMusicIdList(musicPlaylist.getMusicIdList());
        this.mMusicRepository.duplicatePlaylist(new InsertDataListener() {
            public void onSuccess() {
                if (MusicPlaylistPresenter.this.mView != null) {
                    ((MusicPlaylistView) MusicPlaylistPresenter.this.mView).onDuplicationPlaylist(musicPlaylist2);
                }
            }

            public void onError() {
                if (MusicPlaylistPresenter.this.mView != null) {
                    ((MusicPlaylistView) MusicPlaylistPresenter.this.mView).onDuplicationPlaylist(null);
                }
            }
        }, musicPlaylist2);
    }

    public void updatePlaylistName(MusicPlaylist musicPlaylist, final String str, final int i) {
        this.mMusicRepository.updatePlaylistName(new InsertDataListener() {
            public void onSuccess() {
                if (MusicPlaylistPresenter.this.mView != null) {
                    ((MusicPlaylistView) MusicPlaylistPresenter.this.mView).onUpdatePlaylistName(i, str, true);
                }
            }

            public void onError() {
                if (MusicPlaylistPresenter.this.mView != null) {
                    ((MusicPlaylistView) MusicPlaylistPresenter.this.mView).onUpdatePlaylistName(i, str, false);
                }
            }
        }, musicPlaylist, str);
    }
}
