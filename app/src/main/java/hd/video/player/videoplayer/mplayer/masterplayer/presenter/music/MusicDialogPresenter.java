package hd.video.player.videoplayer.mplayer.masterplayer.presenter.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicArtist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.MusicDialogView;

public class MusicDialogPresenter extends BasePresenter<MusicDialogView> {
    private final MusicDataRepository mMusicRepository;

    public MusicDialogPresenter(MusicDialogView musicDialogView, MusicDataRepository musicDataRepository) {
        super(musicDialogView);
        this.mMusicRepository = musicDataRepository;
    }


    public void getAllFavoriteMusic() {
        this.mMusicRepository.fetchFavoriteMusic(new LoadDataListener<MusicInfo>() {
            public void onError() {
            }

            public void onSuccess(List<MusicInfo> list) {
                if (MusicDialogPresenter.this.mView != null) {
                    ((MusicDialogView) MusicDialogPresenter.this.mView).onMusicLoader(list);
                }
            }
        });
    }

    public void getAllMusicOfArtist(MusicArtist musicArtist) {
        if (this.mView != null) {
            ((MusicDialogView) this.mView).onMusicLoader(musicArtist.getMusicList());
        }
    }

    public void getAllMusicOfPlaylist(MusicPlaylist musicPlaylist) {
        this.mMusicRepository.fetchMusicOfPlaylist(musicPlaylist, new LoadDataListener<MusicInfo>() {
            public void onError() {
            }

            public void onSuccess(List<MusicInfo> list) {
                if (MusicDialogPresenter.this.mView != null) {
                    ((MusicDialogView) MusicDialogPresenter.this.mView).onMusicLoader(list);
                }
            }
        });
    }

}
