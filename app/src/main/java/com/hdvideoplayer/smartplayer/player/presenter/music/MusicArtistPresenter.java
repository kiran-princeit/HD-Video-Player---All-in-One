package com.hdvideoplayer.smartplayer.player.presenter.music;

import android.content.Context;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicArtist;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.music.MusicArtistView;


public class MusicArtistPresenter extends BasePresenter<MusicArtistView> {
    private Context context;
    private MusicDataRepository musicRepository;

    public MusicArtistPresenter(Context context, MusicArtistView musicArtistView, MusicDataRepository musicDataRepository) {
        super(musicArtistView);
        this.musicRepository = musicDataRepository;
        this.context = context;
    }

    /**
     * Loads the music artists and updates the view.
     */
    public void loadArtists() {
        this.musicRepository.fetchAllArtists(new LoadDataListener<MusicArtist>() {
            @Override
            public void onError() {
                // Handle error if needed
            }

            @Override
            public void onSuccess(List<MusicArtist> artists) {
                if (MusicArtistPresenter.this.mView != null) {
                    ((MusicArtistView) MusicArtistPresenter.this.mView).onOpenArtist(artists);
                }
            }
        });
    }
}

