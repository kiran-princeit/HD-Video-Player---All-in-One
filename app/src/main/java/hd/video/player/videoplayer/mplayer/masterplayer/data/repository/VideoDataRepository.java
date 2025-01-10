package hd.video.player.videoplayer.mplayer.masterplayer.data.repository;

import android.content.Context;
import android.util.Log;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.VideoDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.VideoDatabaseDataSource;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.VideoStorageDataSource;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoFolder;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoHistory;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoSubtitle;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.InsertDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;

public class VideoDataRepository {
    private static final int TIME_DELAY = 300;
    private final VideoDatabaseDataSource videoDatabaseDataSource;
    private final VideoStorageDataSource videoStorageDataSource;

    public VideoDataRepository(Context context) {
        this.videoStorageDataSource = new VideoStorageDataSource(context);
        this.videoDatabaseDataSource = new VideoDatabaseDataSource(context);
    }

    // Retrieve all videos from storage
    public void fetchAllVideos(final LoadDataListener<VideoInfo> dataLoadListener) {
        Single.defer(() -> Single.just(videoStorageDataSource.getAllVideoFromStorage()))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<VideoInfo>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(List<VideoInfo> videoList) {
                        dataLoadListener.onSuccess(videoList);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dataLoadListener.onError();
                    }
                });
    }

    // Retrieve all video folders
    public void fetchAllVideoFolders(final LoadDataListener<VideoFolder> dataLoadListener) {
        Single.defer(() -> Single.just(videoStorageDataSource.getAllVideoOfFolder()))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<VideoFolder>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(List<VideoFolder> folderList) {
                        dataLoadListener.onSuccess(folderList);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dataLoadListener.onError();
                    }
                });
    }

    public void fetchAllPlaylistVideos(final LoadDataListener<Playlist> loadDataListener) {
        Single.defer(new Callable() {
            public final Object call() throws Exception {
                return Single.just(videoDatabaseDataSource.getAllPlaylistVideos());
            }
        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<Playlist>>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onSuccess(List<Playlist> list) {
                loadDataListener.onSuccess(list);
            }

            public void onError(Throwable th) {
                loadDataListener.onError();
            }
        });
    }

    public void getHistoryVideos(final LoadDataListener<VideoHistory> loadDataListener) {
        Single.defer(new Callable() {
            public final Object call() throws Exception {
                return Single.just(videoDatabaseDataSource.getHistoryVideos());
            }
        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<VideoHistory>>() {
            public void onSubscribe(Disposable disposable) {
            }

            public void onSuccess(List<VideoHistory> list) {
                loadDataListener.onSuccess(list);
            }

            public void onError(Throwable th) {
                loadDataListener.onError();
            }
        });
    }


    // Create a new playlist
    public void createNewPlaylist(final InsertDataListener dataInsertListener, final Playlist playlist) {
        Single.defer(() -> Single.just(videoDatabaseDataSource.createVideoPlaylist(playlist)))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        if (success) {
                            dataInsertListener.onSuccess();
                        } else {
                            dataInsertListener.onError();
                        }
                    }
                });
    }

    // Duplicate a playlist
    public void duplicatePlaylist(final InsertDataListener dataInsertListener, final Playlist playlist) {
        Single.defer(() -> Single.just(videoDatabaseDataSource.duplicateVideoPlaylist(playlist)))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        if (success) {
                            dataInsertListener.onSuccess();
                        } else {
                            dataInsertListener.onError();
                        }
                    }
                });
    }

    // Update the name of a playlist
    public void updatePlaylistName(final InsertDataListener dataInsertListener, final Playlist playlist, final String newName) {
        Single.defer(() -> Single.just(videoDatabaseDataSource.updatePlaylistName(playlist, newName)))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        if (success) {
                            dataInsertListener.onSuccess();
                        } else {
                            dataInsertListener.onError();
                        }
                    }
                });
    }

    public void deletePlaylist(Playlist playlist) {
        this.videoDatabaseDataSource.deletePlaylist(playlist);
    }

    public void deleteAHistoryVideoById(long j) {
        this.videoDatabaseDataSource.deleteVideoHistoryById(j);
    }

    public void deleteAllHistoryVideo() {
        this.videoDatabaseDataSource.deleteAllVideoHistory();
    }

    public void updateVideoHistoryData(VideoInfo videoInfo) {
        this.videoDatabaseDataSource.updateVideoHistoryData(videoInfo);
    }

    public void updateVideoTimeData(long j, long j2) {
        this.videoDatabaseDataSource.updateVideoTimeData(j, j2);
    }

    public int getAVideoTimeData(long j) {
        return this.videoDatabaseDataSource.getAVideoTimeData(j);
    }


    public void fetchAllFavoriteVideos(final LoadDataListener<VideoInfo> dataLoadListener) {
        Single.defer(() -> Single.just(videoDatabaseDataSource.getAllFavoriteVideo()))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<VideoInfo>>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(List<VideoInfo> favoriteList) {
                        dataLoadListener.onSuccess(favoriteList);
                    }
                });
    }

    // Retrieve all recently watched videos
    public void fetchAllRecentlyWatchedVideos(final LoadDataListener<VideoInfo> dataLoadListener) {
        Single.defer(() -> Single.just(VideoDatabaseControl.getInstance().getAllRecentlyVideo()))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<VideoInfo>>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(List<VideoInfo> recentVideos) {
                        dataLoadListener.onSuccess(recentVideos);
                    }
                });
    }

    // Fetch videos in a playlist
    public void fetchVideosInPlaylist(final Playlist playlist, final LoadDataListener<VideoInfo> dataLoadListener) {
        Single.defer(() -> Single.just(videoDatabaseDataSource.getAllVideoOfPlaylist(playlist)))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<VideoInfo>>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(List<VideoInfo> playlistVideos) {
                        dataLoadListener.onSuccess(playlistVideos);
                    }
                });
    }

    // Search videos by name
    public void searchVideosByName(final LoadDataListener<VideoInfo> dataLoadListener, final String videoName) {
        Single.defer(() -> Single.just(videoDatabaseDataSource.searchVideoByVideoName(videoName)))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<VideoInfo>>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(List<VideoInfo> searchResults) {
                        dataLoadListener.onSuccess(searchResults);
                    }
                });
    }


    // Retrieve all subtitle files
    public void fetchAllSubtitleFiles(final LoadDataListener<VideoSubtitle> dataLoadListener) {
        Single.defer(() -> Single.just(videoStorageDataSource.getAllSubFile()))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<VideoSubtitle>>() {
                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(List<VideoSubtitle> subtitleFiles) {
                        dataLoadListener.onSuccess(subtitleFiles);
                    }
                });
    }


    public void getAllVideoHidden(final LoadDataListener<VideoInfo> dataLoadListener) {
        Single.defer(() -> Single.just(videoDatabaseDataSource.getAllVideoHidden()))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<VideoInfo>>() {
                    @Override
                    public void onError(Throwable throwable) {
                        dataLoadListener.onError();
                    }

                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onSuccess(List<VideoInfo> subtitleFiles) {
                        dataLoadListener.onSuccess(subtitleFiles);
                    }
                });
    }

}
