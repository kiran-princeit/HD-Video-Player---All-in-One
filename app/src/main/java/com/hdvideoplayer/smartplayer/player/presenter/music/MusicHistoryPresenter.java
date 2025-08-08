package com.hdvideoplayer.smartplayer.player.presenter.music;

import android.content.Context;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicHistory;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.music.MusicHistoryView;

public class MusicHistoryPresenter extends BasePresenter<MusicHistoryView> {
    private Context mContext;
    private MusicDataRepository mMusicRepository;

    public MusicHistoryPresenter(Context context, MusicHistoryView musicHistoryView, MusicDataRepository musicDataRepository) {
        super(musicHistoryView);
        this.mMusicRepository = musicDataRepository;
        this.mContext = context;
    }

    public void openMusicHistoryTab() {
        this.mMusicRepository.fetchMusicHistory(new LoadDataListener<MusicHistory>() {
            public void onError() {
            }

            public void onSuccess(List<MusicHistory> list) {
                if (MusicHistoryPresenter.this.mView != null) {
                    ((MusicHistoryView) MusicHistoryPresenter.this.mView).updateMusicHistoryList(list);
                }
            }
        });
    }

    public void deleteHistoryMusicById(long j) {
        this.mMusicRepository.deleteAHistoryMusicById(j);
    }

    public void deleteAllMusicHistory() {
        this.mMusicRepository.deleteAllMusicHistory();
    }


}
