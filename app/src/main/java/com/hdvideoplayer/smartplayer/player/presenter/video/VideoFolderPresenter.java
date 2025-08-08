package com.hdvideoplayer.smartplayer.player.presenter.video;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoFolder;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.VideoDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.video.VideoFolderView;

public class VideoFolderPresenter extends BasePresenter<VideoFolderView> {
    private VideoDataRepository mVideoRepository;

    public VideoFolderPresenter(VideoFolderView videoFolderView, VideoDataRepository videoDataRepository) {
        super(videoFolderView);
        this.mVideoRepository = videoDataRepository;
    }

    public void openFoldersTab() {
        this.mVideoRepository.fetchAllVideoFolders(new LoadDataListener<VideoFolder>() {
            public void onError() {
            }

            public void onSuccess(List<VideoFolder> list) {
                if (VideoFolderPresenter.this.mView != null) {
                    ((VideoFolderView) VideoFolderPresenter.this.mView).updateFolderList(list);
                }
            }
        });
    }
}
