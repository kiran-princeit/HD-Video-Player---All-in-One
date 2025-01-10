package hd.video.player.videoplayer.mplayer.masterplayer.presenter.video;

import android.content.Context;
import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.VideoDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.video.VideoInfoView;

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
