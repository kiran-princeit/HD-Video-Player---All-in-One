package hd.video.player.videoplayer.mplayer.masterplayer.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadata.Builder;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.activities.PlayMusicActivity;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.SettingPreferences;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MusicPlayerEqualizerDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.music.MusicPlayerPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant.IntentExtra;
import hd.video.player.videoplayer.mplayer.masterplayer.util.music.MusicPlayerUtils;

public class MusicService extends Service {
    public static int PAUSE = 2;
    public static int PLAY = 1;
    private OnSongCallBack callBack;
    private MusicInfo currentSong;
    private final IBinder iBinder = new MyBinder();
    private int index;
    private final ArrayList<Integer> indexShuffle = new ArrayList();
    private boolean isShuffle = false;
    private MediaSessionCompat mediaSession;
    private final List<Long> musicIdList = new ArrayList();
    private final List<MusicInfo> musicInfoList = new ArrayList();
    private MusicPlayerPresenter musicPlayerPresenter;
    public MediaPlayer player = new MediaPlayer();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                String string = intent.getExtras().getString("actionname");
                if (string != null) {
                    switch (string) {
                        case MusicNotificationReceiver.ACTION_NEXT:
                            MusicService.this.nextSong();
                            break;
                        case MusicNotificationReceiver.ACTION_PLAY:
                            MusicService.this.playSong();
                            break;
                        case MusicNotificationReceiver.ACTION_PREV:
                            MusicService.this.backSong();
                            break;
                        case MusicNotificationReceiver.ACTION_CLOSE:
                            MusicService.this.stopService();
                            break;
                        default:
                            // Handle unknown actions if needed
                            break;
                    }
                }
            }
        }
    };
    private int repeatState = -1;
    private int state;
    private final BroadcastReceiver stopMusicReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MusicService.this.stopService();
        }
    };

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public interface OnSongCallBack {
        void notifyError(Exception exception);

        void onStopService();

        void updatePlayPauseState(boolean z);

        void updateUISong(MusicInfo musicInfo, int i);
    }

    public void setMusicInfoList(List<MusicInfo> list) {
        this.musicInfoList.clear();
        this.musicInfoList.addAll(list);
        this.musicIdList.clear();
        int i = 0;
        for (MusicInfo id : list) {
            this.musicIdList.add(Long.valueOf(id.getId()));
            this.indexShuffle.add(Integer.valueOf(i));
            i++;
        }
        Collections.shuffle(this.indexShuffle);
    }

    public List<Long> getMusicIdList() {
        return this.musicIdList;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public void setState(int i) {
        this.state = i;
    }

    public void setCallBack(OnSongCallBack onSongCallBack) {
        this.callBack = onSongCallBack;
    }

    public void playSong() {
        int i = this.state;
        MediaPlayer mediaPlayer;
        OnSongCallBack onSongCallBack;
        if (i == PLAY) {
            mediaPlayer = this.player;
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
            this.state = PAUSE;
            onSongCallBack = this.callBack;
            if (onSongCallBack != null) {
                onSongCallBack.updatePlayPauseState(false);
            }
        } else if (i == PAUSE) {
            mediaPlayer = this.player;
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
            sendBroadcastPauseVideo();
            this.state = PLAY;
            onSongCallBack = this.callBack;
            if (onSongCallBack != null) {
                onSongCallBack.updatePlayPauseState(true);
            }
        }
        showNotification();
    }

    public void playMusic() {
        try {
            this.state = PLAY;
            MediaPlayer mediaPlayer = this.player;
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                int i = this.index;
                if (i >= 0 && i < this.musicInfoList.size()) {
                    MusicInfo musicInfo = (MusicInfo) this.musicInfoList.get(this.index);
                    this.currentSong = musicInfo;
                    if (TextUtils.isEmpty(musicInfo.getPath())) {
                        this.player.setDataSource(this, Uri.parse(this.currentSong.getUri()));
                    } else {
                        this.player.setDataSource(this.currentSong.getPath());
                        this.musicPlayerPresenter.updateMusicHistoryData(this.currentSong);
                    }
                    FirebaseAnalyticsUtils.putEventPlay(this, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_PLAYER, this.currentSong.getDisplayName(), this.currentSong.getPath());
                    this.player.prepare();
                    this.player.start();
                    sendBroadcastPauseVideo();
                }
            }
            OnSongCallBack onSongCallBack = this.callBack;
            if (onSongCallBack != null) {
                onSongCallBack.updateUISong(this.currentSong, this.index);
            }
            showNotification();
        } catch (Exception e) {
            e.printStackTrace();
            OnSongCallBack onSongCallBack2 = this.callBack;
            if (onSongCallBack2 != null) {
                onSongCallBack2.notifyError(e);
            }
        }
    }

    public void nextSong() {
        if (this.index >= this.musicInfoList.size() - 1) {
            this.index = -1;
        }
        int i = this.index + 1;
        this.index = i;
        if (this.isShuffle && i >= 0 && i < this.indexShuffle.size()) {
            this.index = ((Integer) this.indexShuffle.get(this.index)).intValue();
        }
        playMusic();
    }

    public void backSong() {
        if (this.index == 0) {
            this.index = this.musicInfoList.size();
        }
        int i = this.index - 1;
        this.index = i;
        if (this.isShuffle && i >= 0 && i < this.indexShuffle.size()) {
            this.index = ((Integer) this.indexShuffle.get(this.index)).intValue();
        }
        playMusic();
    }

    public void stopService() {
        sendBroadcastCurrentMusic(null);
        OnSongCallBack onSongCallBack = this.callBack;
        if (onSongCallBack != null) {
            onSongCallBack.onStopService();
        }
        stopSelf();
        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        }
        if (notificationManager != null) {
            notificationManager.cancel(1);
        }
    }

    public void onCreate() {
        super.onCreate();
        if (this.player == null) {
            this.player = new MediaPlayer();
        }
        final SettingPreferences settingPreferences = new SettingPreferences(this);
        this.player.setOnCompletionListener(new OnCompletionListener() {
            public final void onCompletion(MediaPlayer mediaPlayer) {
                MusicService.this.OnCompelete(settingPreferences, mediaPlayer);
            }
        });
        this.musicPlayerPresenter = new MusicPlayerPresenter(new MusicDataRepository(this));
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat((Context) this, "play audio");
        this.mediaSession = mediaSessionCompat;
        mediaSessionCompat.setMetadata(MediaMetadataCompat.fromMediaMetadata(new Builder().putLong(MediaMetadata.METADATA_KEY_DURATION, -1).build()));
        registerReceiver(this.receiver, new IntentFilter("MediaPlayer"));
        LocalBroadcastManager.getInstance(this).registerReceiver(this.stopMusicReceiver, new IntentFilter("RECEIVER_STOP_MUSIC"));
    }

    public void OnCompelete(SettingPreferences settingPreferences, MediaPlayer mediaPlayer) {
        int i = this.repeatState;
        OnSongCallBack onSongCallBack;
        if (i != 0) {
            if (i == 1) {
                playMusic();
            } else if (i == 2) {
                if (settingPreferences.isAutoPlayNextMusic()) {
                    nextSong();
                } else {
                    this.player.pause();
                    this.state = PAUSE;
                    onSongCallBack = this.callBack;
                    if (onSongCallBack != null) {
                        onSongCallBack.updatePlayPauseState(false);
                    }
                    showNotification();
                }
            }
        } else if (!settingPreferences.isAutoPlayNextMusic()) {
            this.player.pause();
            this.state = PAUSE;
            onSongCallBack = this.callBack;
            if (onSongCallBack != null) {
                onSongCallBack.updatePlayPauseState(false);
            }
            showNotification();
        } else if (this.isShuffle || this.index != this.musicInfoList.size() - 1) {
            nextSong();
        } else {
            this.player.pause();
            this.state = PAUSE;
            onSongCallBack = this.callBack;
            if (onSongCallBack != null) {
                onSongCallBack.updatePlayPauseState(false);
            }
            showNotification();
        }
    }

    public IBinder onBind(Intent intent) {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        return this.iBinder;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        createNotificationChanel();
        showNotification();
        return Service.START_STICKY;
    }

    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    MusicNotificationReceiver.CHANNEL_ID,
                    "Chanel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Chanel Description");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public void showNotification() {
        MusicInfo currentSong = getCurrentSong();
        sendBroadcastCurrentMusic(currentSong);

        if (currentSong == null) {
            stopForeground(true);
            return;
        }

        int playPauseIcon = (this.state == PLAY) ? R.drawable.ic_noti_pause : R.drawable.ic_noti_play;
        Bitmap largeIcon = MusicPlayerUtils.getThumbnailOfSong(this, currentSong.getPath(), 128);

        // Create intents for notification actions
        PendingIntent activity = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, PlayMusicActivity.class)
                        .putExtra(IntentExtra.EXTRA_FROM_NOTIFICATION, true)
                        .putExtra(IntentExtra.EXTRA_MUSIC_NUMBER, this.index)
                        .putExtra(IntentExtra.EXTRA_MUSIC_SONG, this.currentSong)
                        .putExtra(IntentExtra.EXTRA_MUSIC_ARRAY, (ArrayList) this.musicIdList)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent broadcastPrev = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(this, MusicNotificationReceiver.class).setAction(MusicNotificationReceiver.ACTION_PREV),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        PendingIntent broadcastPlay = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(this, MusicNotificationReceiver.class).setAction(MusicNotificationReceiver.ACTION_PLAY),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        PendingIntent broadcastNext = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(this, MusicNotificationReceiver.class).setAction(MusicNotificationReceiver.ACTION_NEXT),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        PendingIntent broadcastClose = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(this, MusicNotificationReceiver.class).setAction(MusicNotificationReceiver.ACTION_CLOSE),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MusicNotificationReceiver.CHANNEL_ID)
//                .setLargeIcon(MusicPlayerUtils.getThumbnailOfSong(this, currentSong.getPath(), 500))
                .setLargeIcon(largeIcon) // Use the bitmap large icon
                .setSmallIcon(R.drawable.logo) // Notification icon
                .setContentTitle(currentSong.getDisplayName()) // Song title
                .setContentText(currentSong.getArtist()) // Artist name
                .setContentIntent(activity)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", broadcastPrev) // Previous button
                .addAction(playPauseIcon, "Play/Pause", broadcastPlay) // Play/Pause button
                .addAction(R.drawable.ic_round_skip_next_24, "Next", broadcastNext) // Next button
                .addAction(R.drawable.ic_noti_close, "Close", broadcastClose)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(1)) // Show play/pause button in compact view
                .setOngoing(this.state == PLAY) // Keep notification ongoing when playing
                .setOnlyAlertOnce(true); // Do not alert repeatedly
        startForeground(1, builder.build());
    }

    public static Bitmap getThumbnailOfSong(Context context, String songPath, int size) {
        // Use Glide or any other method to load the image and resize it
        Glide.with(context)
                .asBitmap()
                .load(songPath) // Your image or URI for album art
                .override(size, size) // Resize to appropriate dimensions
                .into(new SimpleTarget<Bitmap>(size, size) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        // You can pass the Bitmap back to your notification setup here
                    }
                });
        return null; // Return the resized bitmap
    }

    public void onDestroy() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (mediaSession != null) {
            mediaSession.release();  // Release MediaSession if it's no longer needed
            mediaSession = null;
        }

        MusicPlayerEqualizerDialogBuilder.release();
        unregisterReceiver(this.receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.stopMusicReceiver);
        super.onDestroy();
    }

    public int getCurrentSeek() {
        MediaPlayer mediaPlayer = this.player;
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getTotalDuration() {
        MediaPlayer mediaPlayer = this.player;
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public void seekTo(int i) {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.seekTo(i);
            } catch (Exception e) {
                OnSongCallBack onSongCallBack = this.callBack;
                if (onSongCallBack != null) {
                    onSongCallBack.notifyError(e);
                }
            }
        }
    }

    public MusicInfo getCurrentSong() {
        return this.currentSong;
    }

    public boolean isSongPlaying() {
        MediaPlayer mediaPlayer = this.player;
        return (mediaPlayer == null || !mediaPlayer.isPlaying() || this.currentSong == null) ? false : true;
    }

    public int getSessionIdMusic() {
        MediaPlayer mediaPlayer = this.player;
        return mediaPlayer != null ? mediaPlayer.getAudioSessionId() : -1;
    }

    public boolean isShuffle() {
        return this.isShuffle;
    }

    public void setShuffle(boolean z) {
        this.isShuffle = z;
    }

    public int getRepeatState() {
        return this.repeatState;
    }

    public void setRepeatState(int i) {
        this.repeatState = i;
    }

    private void sendBroadcastCurrentMusic(MusicInfo musicInfo) {
        Intent intent = new Intent("RECEIVER_CURRENT_MUSIC");
        intent.putExtra(IntentExtra.EXTRA_MUSIC_SONG, musicInfo);
        intent.putExtra(IntentExtra.EXTRA_MUSIC_PLAYING, this.state == PLAY);
        intent.putExtra(IntentExtra.EXTRA_MUSIC_NUMBER, this.index);
        intent.putExtra(IntentExtra.EXTRA_MUSIC_ARRAY, (ArrayList) this.musicIdList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBroadcastPauseVideo() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("RECEIVER_STOP_VIDEO"));
    }


    //    public void showNotification() {
//        MusicInfo currentSong = getCurrentSong();
//        sendBroadcastCurrentMusic(currentSong);
//
//        if (currentSong == null) {
//            stopForeground(true);
//            return;
//        }
//
//        int playPauseIcon = (this.state == PLAY) ? R.drawable.ic_noti_pause : R.drawable.ic_noti_play;
//
//        Intent intent = new Intent(this, PlayMusicActivity.class);
//        intent.putExtra(IntentExtra.EXTRA_FROM_NOTIFICATION, true);
//        intent.putExtra(IntentExtra.EXTRA_MUSIC_NUMBER, this.index);
//        intent.putExtra(IntentExtra.EXTRA_MUSIC_SONG, this.currentSong);
//        intent.putExtra(IntentExtra.EXTRA_MUSIC_ARRAY, (ArrayList) this.musicIdList);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent activity = PendingIntent.getActivity(
//                this,
//                0,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        PendingIntent broadcastPrev = PendingIntent.getBroadcast(
//                this,
//                0,
//                new Intent(this, MusicNotificationReceiver.class).setAction(MusicNotificationReceiver.ACTION_PREV),
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        PendingIntent broadcastPlay = PendingIntent.getBroadcast(
//                this,
//                0,
//                new Intent(this, MusicNotificationReceiver.class).setAction(MusicNotificationReceiver.ACTION_PLAY),
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        PendingIntent broadcastNext = PendingIntent.getBroadcast(
//                this,
//                0,
//                new Intent(this, MusicNotificationReceiver.class).setAction(MusicNotificationReceiver.ACTION_NEXT),
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        PendingIntent broadcastClose = PendingIntent.getBroadcast(
//                this,
//                0,
//                new Intent(this, MusicNotificationReceiver.class).setAction(MusicNotificationReceiver.ACTION_CLOSE),
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        Notification notification = new NotificationCompat.Builder(this, MusicNotificationReceiver.CHANNEL_ID)
//                .setContentTitle(currentSong.getDisplayName())
//                .setContentText(currentSong.getArtist())
//                .setLargeIcon(MusicPlayerUtils.getThumbnailOfSong(this, currentSong.getPath(), 80))
//                .setSmallIcon(R.drawable.logo)
//                .setContentIntent(activity)
//                .addAction(R.drawable.ic_noti_prev, "Previous", broadcastPrev)
//                .addAction(playPauseIcon, "Play", broadcastPlay)
//                .addAction(R.drawable.ic_noti_next, "Next", broadcastNext)
//                .addAction(R.drawable.ic_noti_close, "Close", broadcastClose)
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .setOnlyAlertOnce(true)
//                .build();
//
//        startForeground(1, notification);
//    }
}
