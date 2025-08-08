package com.hdvideoplayer.smartplayer.player.presenter.music;

import android.content.Context;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicAlbum;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.music.MusicAlbumView;

public class MusicAlbumPresenter extends BasePresenter<MusicAlbumView> {
    private Context context;
    private MusicDataRepository musicRepository;

    public MusicAlbumPresenter(Context context, MusicAlbumView musicAlbumView, MusicDataRepository musicDataRepository) {
        super(musicAlbumView);
        this.musicRepository = musicDataRepository;
        this.context = context;
    }

    // Method to load all music albums
    public void loadMusicAlbumList() {
        musicRepository.fetchAllMusicAlbums(new LoadDataListener<MusicAlbum>() {
            @Override
            public void onError() {
                // Handle error
            }

            @Override
            public void onSuccess(List<MusicAlbum> albumList) {
                if (mView != null) {
                    ((MusicAlbumView) MusicAlbumPresenter.this.mView).onUpdateAlbum(albumList);
                }
            }
        });
    }

    // Method to load all music tracks for a specific album
    public void loadMusicListForAlbum(MusicAlbum musicAlbum) {
        musicRepository.fetchMusicOfAlbum(new LoadDataListener<MusicInfo>() {
            @Override
            public void onError() {
                // Handle error
            }

            @Override
            public void onSuccess(List<MusicInfo> musicList) {
                if (mView != null) {
                    ((MusicAlbumView) MusicAlbumPresenter.this.mView).onUpdateMusicList(musicList);
//                    mView.onUpdateMusicList(musicList);
                }
            }
        }, musicAlbum);
    }


}
