package com.hdvideoplayer.smartplayer.player.presenter.video;

import android.content.Context;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.VideoDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.video.VideoSearchView;

public class VideoSearchingPresenter extends BasePresenter<VideoSearchView> {
    private Context mContext;
    private VideoDataRepository mVideoRepository;

    public VideoSearchingPresenter(Context context, VideoSearchView videoSearchView, VideoDataRepository videoDataRepository) {
        super(videoSearchView);
        this.mVideoRepository = videoDataRepository;
        this.mContext = context;
    }

    public void searchVideoByVideoName(String str) {
        this.mVideoRepository.searchVideosByName(new LoadDataListener<VideoInfo>() {
            public void onError() {
            }

            public void onSuccess(List<VideoInfo> list) {
                if (VideoSearchingPresenter.this.mView != null) {
                    ((VideoSearchView) VideoSearchingPresenter.this.mView).onSearchVideo(list);
                }
            }
        }, str);
    }
}
