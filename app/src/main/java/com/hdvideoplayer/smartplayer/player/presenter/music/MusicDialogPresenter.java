package com.hdvideoplayer.smartplayer.player.presenter.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicArtist;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicPlaylist;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.MusicDialogView;

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
