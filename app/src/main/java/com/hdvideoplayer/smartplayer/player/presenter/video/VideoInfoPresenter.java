package com.hdvideoplayer.smartplayer.player.presenter.video;

import android.content.Context;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.VideoDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.video.VideoInfoView;

public class VideoInfoPresenter extends BasePresenter<VideoInfoView> {
    private Context mContext;
    private VideoDataRepository mVideoRepository;

    public VideoInfoPresenter(Context context, VideoInfoView videoInfoView, VideoDataRepository videoDataRepository) {
        super(videoInfoView);
        this.mVideoRepository = videoDataRepository;
        this.mContext = context;
    }

    public void openVideosTab() {
        this.mVideoRepository.fetchAllVideos(new LoadDataListener<VideoInfo>() {
            public void onError() {
            }

            public void onSuccess(List<VideoInfo> list) {
                if (VideoInfoPresenter.this.mView != null) {
                    ((VideoInfoView) VideoInfoPresenter.this.mView).updateVideoList(list);
                }
            }
        });
    }
}
