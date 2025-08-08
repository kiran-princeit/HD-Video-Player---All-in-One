package com.hdvideoplayer.smartplayer.player.data.repository;

import android.content.Context;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.hdvideoplayer.smartplayer.player.data.datasource.MusicDatabaseDataSource;
import com.hdvideoplayer.smartplayer.player.data.datasource.MusicStorageDataSource;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicAlbum;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicArtist;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicHistory;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicPlaylist;

//public class MusicDataRepository {
//    private static final int TIME_DELAY = 300;
//    private Context mContext;
//    private MusicDatabaseDataSource musicDatabaseDataSource;
//    private MusicStorageDataSource musicStorageDataSource ;
//
//    public MusicDataRepository(Context context) {
//        this.mContext = context;
//        musicStorageDataSource  = new MusicStorageDataSource(mContext);
//        musicDatabaseDataSource = new MusicDatabaseDataSource(mContext);
//    }
//
//    public void getAllMusics(final LoadDataListener<MusicInfo> loadDataListener) {
//        Single.defer(new Callable() {
//            public final Object call() throws Exception {
//                return MusicDataRepository.this.m476xb234415e();
//            }
//        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicInfo>>() {
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicInfo> list) {
//                loadDataListener.onSuccess(list);
//            }
//
//            public void onError(Throwable th) {
//                loadDataListener.onError();
//            }
//        });
//    }
//
//    public SingleSource m476xb234415e() throws Exception {
//        return Single.just(this.musicStorageDataSource.getAllMusicFromStorage());
//    }
//
//    public SingleSource m480xa1fd1a0c(MusicPlaylist musicPlaylist, String str) throws Exception {
//        return Single.just(Boolean.valueOf(this.musicDatabaseDataSource.updatePlaylistName(musicPlaylist, str)));
//    }
//
//    public void updatePlaylistName(final InsertDataListener insertDataListener, final MusicPlaylist musicPlaylist, final String str) {
//        Single.defer(new Callable() {
//            public final Object call() throws Exception {
//                return MusicDataRepository.this.m480xa1fd1a0c(musicPlaylist, str);
//            }
//        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Boolean>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(Boolean bool) {
//                if (bool.booleanValue()) {
//                    insertDataListener.onSuccess();
//                } else {
//                    insertDataListener.onError();
//                }
//            }
//        });
//    }
//
//    public void duplicateMusicPlaylist(final InsertDataListener insertDataListener, final MusicPlaylist musicPlaylist) {
//        Single.defer(new Callable() {
//            public final Object call() throws Exception {
//                return MusicDataRepository.this.m473xb0bf18f3(musicPlaylist);
//            }
//        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Boolean>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(Boolean bool) {
//                if (bool.booleanValue()) {
//                    insertDataListener.onSuccess();
//                } else {
//                    insertDataListener.onError();
//                }
//            }
//        });
//    }
//
//    public SingleSource m473xb0bf18f3(MusicPlaylist musicPlaylist) throws Exception {
//        return Single.just(Boolean.valueOf(this.musicDatabaseDataSource.duplicateMusicPlaylist(musicPlaylist)));
//    }
//
//    public void getAllFavoriteMusic(final LoadDataListener<MusicInfo> loadDataListener) {
//        Single.defer(new Callable() {
//            public final Object call() throws Exception {
//                return MusicDataRepository.this.m474x501bb48c();
//            }
//        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicInfo>>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicInfo> list) {
//                Log.d("aaa", "musics = " + list.size());
//                loadDataListener.onSuccess(list);
//            }
//        });
//    }
//
//    public SingleSource m474x501bb48c() throws Exception {
//        return Single.just(this.musicDatabaseDataSource.getAllFavoriteMusic());
//    }
//
//    public void getAllPlaylistMusics(final LoadDataListener<MusicPlaylist> loadDataListener) {
//        Single.defer(new Callable() {
//            public final Object call() throws Exception {
//                return MusicDataRepository.this.m477xc88b5154();
//            }
//        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicPlaylist>>() {
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicPlaylist> list) {
//                loadDataListener.onSuccess(list);
//            }
//
//            public void onError(Throwable th) {
//                loadDataListener.onError();
//            }
//        });
//    }
//
//    public SingleSource m477xc88b5154() throws Exception {
//        return Single.just(this.musicDatabaseDataSource.getAllPlaylistMusics());
//    }
//
//    public void getAllMusicOfPlaylist(final MusicPlaylist musicPlaylist, final LoadDataListener<MusicInfo> loadDataListener) {
//        Single.defer(new Callable() {
//            public final Object call() throws Exception {
//                return MusicDataRepository.this.m475x587dc233(musicPlaylist);
//            }
//        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicInfo>>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicInfo> list) {
//                loadDataListener.onSuccess(list);
//            }
//        });
//    }
//
//    public SingleSource m475x587dc233(MusicPlaylist musicPlaylist) throws Exception {
//        return Single.just(this.musicDatabaseDataSource.getAllMusicOfPlaylist(musicPlaylist));
//    }
//
//    public void getHistoryMusics(final LoadDataListener<MusicHistory> loadDataListener) {
//        Single.defer(new Callable() {
//            public final Object call() throws Exception {
//                return MusicDataRepository.this.m478x5461b3d7();
//            }
//        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicHistory>>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicHistory> list) {
//                Log.d("aaa", "musics = " + list.size());
//                loadDataListener.onSuccess(list);
//            }
//        });
//    }
//
//    public SingleSource m478x5461b3d7() throws Exception {
//        return Single.just(this.musicDatabaseDataSource.getHistoryMusics());
//    }
//
//    public void createMusicPlaylist(final InsertDataListener insertDataListener, final MusicPlaylist musicPlaylist) {
//        Single.defer(new Callable() {
//            public final Object call() throws Exception {
//                return MusicDataRepository.this.m472x5101f58d(musicPlaylist);
//            }
//        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Boolean>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(Boolean bool) {
//                if (bool.booleanValue()) {
//                    insertDataListener.onSuccess();
//                } else {
//                    insertDataListener.onError();
//                }
//            }
//        });
//    }
//
//    public SingleSource m472x5101f58d(MusicPlaylist musicPlaylist) throws Exception {
//        return Single.just(Boolean.valueOf(this.musicDatabaseDataSource.createMusicPlaylist(musicPlaylist)));
//    }
//
//    public void updateMusicHistoryData(MusicInfo musicInfo) {
//        this.musicDatabaseDataSource.updateMusicHistoryData(musicInfo);
//    }
//
//    public void updateMusicListForPlaylist(MusicPlaylist musicPlaylist, List<MusicInfo> list) {
//        this.musicDatabaseDataSource.updateMusicListForPlaylist(musicPlaylist, list);
//    }
//
//    public void deletePlaylist(MusicPlaylist musicPlaylist) {
//        this.musicDatabaseDataSource.deletePlaylist(musicPlaylist);
//    }
//
//    public void getAllMusicAlbum(final LoadDataListener<MusicAlbum> loadDataListener) {
//        Single.just(this.musicStorageDataSource.getAllMusicAlbum()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicAlbum>>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicAlbum> list) {
//                loadDataListener.onSuccess(list);
//            }
//        });
//    }
//
//    public void getAllMusicArtist(final LoadDataListener<MusicArtist> loadDataListener) {
//        Single.just(this.musicStorageDataSource.getAllMusicArtist()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicArtist>>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicArtist> list) {
//                loadDataListener.onSuccess(list);
//            }
//        });
//    }
//
//    public void getAllAlbumOfArtist(final LoadDataListener<MusicAlbum> loadDataListener, MusicArtist musicArtist) {
//        Single.just(this.musicStorageDataSource.getAllAlbumOfArtist(musicArtist)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicAlbum>>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicAlbum> list) {
//                loadDataListener.onSuccess(list);
//            }
//        });
//    }
//
//    public void getAllMusicOfAlbum(final LoadDataListener<MusicInfo> loadDataListener, MusicAlbum musicAlbum) {
//        Single.just(this.musicStorageDataSource.getAllMusicFromAlbum(musicAlbum.getAlbumId())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicInfo>>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicInfo> list) {
//                loadDataListener.onSuccess(list);
//            }
//        });
//    }
//
//    public void getAllSongsOfArtist(final LoadDataListener<MusicInfo> loadDataListener, MusicArtist musicArtist) {
//        Single.just(this.musicStorageDataSource.getAllSongsOfArtist(musicArtist.getArtistId())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicInfo>>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicInfo> list) {
//                loadDataListener.onSuccess(list);
//            }
//        });
//    }
//
//    public void deleteAHistoryMusicById(long j) {
//        this.musicDatabaseDataSource.deleteMusicHistoryById(j);
//    }
//
//    public SingleSource m479x11509f49(String str) throws Exception {
//        return Single.just(this.musicDatabaseDataSource.searchMusicByMusicName(str));
//    }
//
//    public void searchMusicByMusicName(final LoadDataListener<MusicInfo> loadDataListener, final String str) {
//        Single.defer(new Callable() {
//            public final Object call() throws Exception {
//                return MusicDataRepository.this.m479x11509f49(str);
//            }
//        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicInfo>>() {
//            public void onError(Throwable th) {
//            }
//
//            public void onSubscribe(Disposable disposable) {
//            }
//
//            public void onSuccess(List<MusicInfo> list) {
//                loadDataListener.onSuccess(list);
//            }
//        });
//    }
//
//    public void deleteAllMusicHistory() {
//        this.musicDatabaseDataSource.deleteAllMusicHistory();
//    }
//}


public class MusicDataRepository {
    private static final int TIME_DELAY = 300;
    private Context mContext;
    private MusicDatabaseDataSource musicDatabaseDataSource;
    private MusicStorageDataSource musicStorageDataSource;

    public MusicDataRepository(Context context) {
        this.mContext = context;
        musicStorageDataSource = new MusicStorageDataSource(mContext);
        musicDatabaseDataSource = new MusicDatabaseDataSource(mContext);
    }

    public void fetchAllMusic(final ILoaderRepository.LoadDataListener<MusicInfo> loadDataListener) {
        Single.defer(() -> fetchAllMusicFromStorage())
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicInfo>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicInfo> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {
                        loadDataListener.onError();
                    }
                });
    }

    private SingleSource<List<MusicInfo>> fetchAllMusicFromStorage() {
        return Single.just(musicStorageDataSource.getAllMusicFromStorage());
    }

    public void updatePlaylistName(final ILoaderRepository.InsertDataListener insertDataListener, final MusicPlaylist playlist, final String newName) {
        Single.defer(() -> updatePlaylistNameInDatabase(playlist, newName))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(Boolean success) {
                        if (success) {
                            insertDataListener.onSuccess();
                        } else {
                            insertDataListener.onError();
                        }
                    }

                    public void onError(Throwable th) {}
                });
    }

    private SingleSource<Boolean> updatePlaylistNameInDatabase(MusicPlaylist playlist, String newName) {
        return Single.just(musicDatabaseDataSource.updatePlaylistName(playlist, newName));
    }

    public void duplicatePlaylist(final ILoaderRepository.InsertDataListener insertDataListener, final MusicPlaylist playlist) {
        Single.defer(() -> duplicatePlaylistInDatabase(playlist))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(Boolean success) {
                        if (success) {
                            insertDataListener.onSuccess();
                        } else {
                            insertDataListener.onError();
                        }
                    }

                    public void onError(Throwable th) {}
                });
    }

    private SingleSource<Boolean> duplicatePlaylistInDatabase(MusicPlaylist playlist) {
        return Single.just(musicDatabaseDataSource.duplicateMusicPlaylist(playlist));
    }

    public void fetchFavoriteMusic(final ILoaderRepository.LoadDataListener<MusicInfo> loadDataListener) {
        Single.defer(this::fetchFavoriteMusicFromDatabase)
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicInfo>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicInfo> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {}
                });
    }

    private SingleSource<List<MusicInfo>> fetchFavoriteMusicFromDatabase() {
        return Single.just(musicDatabaseDataSource.getAllFavoriteMusic());
    }

    public void fetchAllPlaylists(final ILoaderRepository.LoadDataListener<MusicPlaylist> loadDataListener) {
        Single.defer(this::fetchAllPlaylistsFromDatabase)
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicPlaylist>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicPlaylist> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {
                        loadDataListener.onError();
                    }
                });
    }

    private SingleSource<List<MusicPlaylist>> fetchAllPlaylistsFromDatabase() {
        return Single.just(musicDatabaseDataSource.getAllPlaylistMusics());
    }

    public void fetchMusicOfPlaylist(final MusicPlaylist playlist, final ILoaderRepository.LoadDataListener<MusicInfo> loadDataListener) {
        Single.defer(() -> fetchMusicOfPlaylistFromDatabase(playlist))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicInfo>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicInfo> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {}
                });
    }

    private SingleSource<List<MusicInfo>> fetchMusicOfPlaylistFromDatabase(MusicPlaylist playlist) {
        return Single.just(musicDatabaseDataSource.getAllMusicOfPlaylist(playlist));
    }

    public void fetchMusicHistory(final ILoaderRepository.LoadDataListener<MusicHistory> loadDataListener) {
        Single.defer(this::fetchMusicHistoryFromDatabase)
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicHistory>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicHistory> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {}
                });
    }

    private SingleSource<List<MusicHistory>> fetchMusicHistoryFromDatabase() {
        return Single.just(musicDatabaseDataSource.getHistoryMusics());
    }

    public void createPlaylist(final ILoaderRepository.InsertDataListener insertDataListener, final MusicPlaylist playlist) {
        Single.defer(() -> createPlaylistInDatabase(playlist))
                .delay(TIME_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(Boolean success) {
                        if (success) {
                            insertDataListener.onSuccess();
                        } else {
                            insertDataListener.onError();
                        }
                    }

                    public void onError(Throwable th) {}
                });
    }

    private SingleSource<Boolean> createPlaylistInDatabase(MusicPlaylist playlist) {
        return Single.just(musicDatabaseDataSource.createMusicPlaylist(playlist));
    }

    public void updateMusicHistory(MusicInfo musicInfo) {
        musicDatabaseDataSource.updateMusicHistoryData(musicInfo);
    }

    public void updatePlaylistMusic(MusicPlaylist playlist, List<MusicInfo> musicList) {
        musicDatabaseDataSource.updateMusicListForPlaylist(playlist, musicList);
    }

    public void deletePlaylist(MusicPlaylist playlist) {
        musicDatabaseDataSource.deletePlaylist(playlist);
    }

    public void fetchAllMusicAlbums(final ILoaderRepository.LoadDataListener<MusicAlbum> loadDataListener) {
        Single.just(musicStorageDataSource.getAllMusicAlbum())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicAlbum>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicAlbum> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {}
                });
    }

    public void fetchAllArtists(final ILoaderRepository.LoadDataListener<MusicArtist> loadDataListener) {
        Single.just(musicStorageDataSource.getAllMusicArtist())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicArtist>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicArtist> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {}
                });
    }

    public void fetchAlbumsOfArtist(final ILoaderRepository.LoadDataListener<MusicAlbum> loadDataListener, MusicArtist artist) {
        Single.just(musicStorageDataSource.getAllAlbumOfArtist(artist))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicAlbum>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicAlbum> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {}
                });
    }

    public void fetchMusicOfAlbum(final ILoaderRepository.LoadDataListener<MusicInfo> loadDataListener, MusicAlbum album) {
        Single.just(musicStorageDataSource.getAllMusicFromAlbum(album.getAlbumId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicInfo>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicInfo> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {}
                });
    }

    public void fetchSongsOfArtist(final ILoaderRepository.LoadDataListener<MusicInfo> loadDataListener, MusicArtist artist) {
        Single.just(musicStorageDataSource.getAllSongsOfArtist(artist.getArtistId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MusicInfo>>() {
                    public void onSubscribe(Disposable disposable) {}

                    public void onSuccess(List<MusicInfo> list) {
                        loadDataListener.onSuccess(list);
                    }

                    public void onError(Throwable th) {}
                });
    }

    public void searchMusicByMusicName(final ILoaderRepository.LoadDataListener<MusicInfo> loadDataListener, final String str) {
        Single.defer(new Callable() {
            public final Object call() throws Exception {
                return Single.just(musicDatabaseDataSource.searchMusicByMusicName(str));
            }
        }).delay(300, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<MusicInfo>>() {
            public void onError(Throwable th) {
            }

            public void onSubscribe(Disposable disposable) {
            }

            public void onSuccess(List<MusicInfo> list) {
                loadDataListener.onSuccess(list);
            }
        });
    }
    public void deleteAHistoryMusicById(long j) {
        this.musicDatabaseDataSource.deleteMusicHistoryById(j);
    }

    public void deleteAllMusicHistory() {
        this.musicDatabaseDataSource.deleteAllMusicHistory();
    }
}

