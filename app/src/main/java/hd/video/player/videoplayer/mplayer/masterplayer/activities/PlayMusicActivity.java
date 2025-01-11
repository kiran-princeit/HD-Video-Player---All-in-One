package hd.video.player.videoplayer.mplayer.masterplayer.activities;

import static hd.video.player.videoplayer.mplayer.masterplayer.MyApplication.isNetworkConnected;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
//import androidx.work.WorkRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.MusicArtPagerAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.NextInMusicPlaylistAdapter.Callback;
import hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.AdManager;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.MusicDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.MusicFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.ChangeVolumeBottomSheet;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MusicPlayerEqualizerDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MusicPlayerNextInDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.services.MusicService;
import hd.video.player.videoplayer.mplayer.masterplayer.services.MusicService.MyBinder;
import hd.video.player.videoplayer.mplayer.masterplayer.services.MusicService.OnSongCallBack;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant.IntentExtra;
import hd.video.player.videoplayer.mplayer.masterplayer.util.music.MusicPlayerUtils;

public class PlayMusicActivity extends BaseActivity implements OnClickListener, ServiceConnection, OnSongCallBack {
    private ImageView btnPlayPause;
    private Handler handler = new Handler();
    private ImageView imgBack;
    private ImageView imgBtnNext;
    private ImageView imgBtnPrev;
    private ImageView imgEqualizer;
    private ImageView imgFavorite;
    private ImageView imgMusicLyric;
    private ImageView imgPlaylist;
    private ImageView imgRepeat;
    private ImageView imgShuffle;
    private ImageView imgVolume;
    private int mPosition;
    private final List<MusicInfo> mSongList = new ArrayList();
    private MusicPlayerNextInDialogBuilder musicBuilder;
    private MusicService musicService;
    private int repeatState = 0;
    private final Runnable runnable = new Runnable() {
        public void run() {
            Log.d("aaa ", "startMediaController");
            if (PlayMusicActivity.this.musicService != null) {
                int currentSeek = PlayMusicActivity.this.musicService.getCurrentSeek();
                int totalDuration = PlayMusicActivity.this.musicService.getTotalDuration();
                PlayMusicActivity.this.seekBar.setProgress(currentSeek);
                PlayMusicActivity.this.seekBar.setMax(totalDuration);
                PlayMusicActivity.this.tvCurrentTime.setText(Utility.convertLongToDuration((long) currentSeek));
                PlayMusicActivity.this.tvTotalTime.setText(Utility.convertLongToDuration((long) totalDuration));
                if (PlayMusicActivity.this.handler != null) {
                    PlayMusicActivity.this.handler.postDelayed(this, 500);
                }
            }
        }
    };

    private SeekBar seekBar;
    private TextView tvArtist;
    private TextView tvCurrentTime;
    private TextView tvSong;
    private TextView tvTitle;
    private TextView tvTotalTime;
    private boolean updateData = false;
    ViewPager2 viewPagerMusicArt;
    private RelativeLayout adContainer;
    private ShimmerFrameLayout shimmerFrameLayout;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_play_music);

        adContainer = findViewById(R.id.adContainer);
        shimmerFrameLayout = findViewById(R.id.shimmer_container_banner);
        loadBannerAd();

        this.btnPlayPause = (ImageView) findViewById(R.id.img_btn_play);
        this.imgBtnPrev = (ImageView) findViewById(R.id.img_btn_previous);
        this.imgBtnNext = (ImageView) findViewById(R.id.img_btn_next);
        this.tvSong = (TextView) findViewById(R.id.tv_song);
        this.tvTitle = (TextView) findViewById(R.id.tv_title);
        this.tvArtist = (TextView) findViewById(R.id.tv_artist);
        this.tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        this.tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
        this.seekBar = (SeekBar) findViewById(R.id.seekbar_controller);
        this.imgBack = (ImageView) findViewById(R.id.iv_back);
        this.imgFavorite = (ImageView) findViewById(R.id.iv_favorite);
        this.imgRepeat = (ImageView) findViewById(R.id.img_repeat);
        this.imgVolume = (ImageView) findViewById(R.id.img_volume);
        this.imgPlaylist = (ImageView) findViewById(R.id.iv_music_playlist);
        this.imgMusicLyric = (ImageView) findViewById(R.id.iv_lyric);
        this.imgShuffle = (ImageView) findViewById(R.id.img_shuffle);
        this.imgEqualizer = (ImageView) findViewById(R.id.img_audio_effect_control);
        viewPagerMusicArt = findViewById(R.id.viewPagerMusicArt);

        this.btnPlayPause.setOnClickListener(this);
        this.imgBtnPrev.setOnClickListener(this);
        this.imgBtnNext.setOnClickListener(this);
        this.imgBack.setOnClickListener(this);
        this.imgFavorite.setOnClickListener(this);
        this.imgRepeat.setOnClickListener(this);
        this.imgVolume.setOnClickListener(this);
        this.imgPlaylist.setOnClickListener(this);
        this.imgMusicLyric.setOnClickListener(this);
        this.imgShuffle.setOnClickListener(this);
        this.imgEqualizer.setOnClickListener(this);

        findViewById(R.id.iv_share).setOnClickListener(view -> {
            shareMusic(PlayMusicActivity.this, mSongList);
        });

        this.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (PlayMusicActivity.this.musicService != null) {
                    PlayMusicActivity.this.musicService.seekTo(seekBar.getProgress());
                }
            }
        });
        if (getIntent() != null) {
            this.mSongList.clear();
            Intent intent = getIntent();
            if (intent.getData() != null) {
                this.mSongList.add(MusicPlayerUtils.getMusicInfoFromUri(this, intent.getData()));
                this.mPosition = 0;
            } else {
                ArrayList arrayList = (ArrayList) intent.getSerializableExtra(IntentExtra.EXTRA_MUSIC_ARRAY);
                if (arrayList != null) {
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        MusicInfo musicById = MusicDatabaseControl.getInstance().getMusicById(((Long) it.next()).longValue());
                        if (musicById != null) {
                            this.mSongList.add(musicById);
                        }
                    }
                }
                MusicInfo musicInfo = (MusicInfo) intent.getSerializableExtra(IntentExtra.EXTRA_MUSIC_SONG);
                if (this.mSongList.isEmpty() && musicInfo != null) {
                    this.mSongList.add(musicInfo);
                }
                this.mPosition = intent.getIntExtra(IntentExtra.EXTRA_MUSIC_NUMBER, 0);
            }
        }
        this.updateData = true;
        try {
            startService(new Intent(this, MusicService.class));
        } catch (IllegalStateException e) {
            Log.d("aaa", "IllegalStateException: " + e.getMessage());
        }
        MusicArtPagerAdapter adapter = new MusicArtPagerAdapter(this, mSongList);
        viewPagerMusicArt.setAdapter(adapter);


        viewPagerMusicArt.setCurrentItem(mPosition, false);
        viewPagerMusicArt.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                mPosition = position;
                MusicInfo currentSong = mSongList.get(position);
                tvTitle.setText(currentSong.getDisplayName());
                tvSong.setText(currentSong.getDisplayName());
                tvArtist.setText(currentSong.getArtist());

                if (musicService != null) {
                    musicService.setIndex(mPosition);  // Set the new song index in the service
                    musicService.playMusic();  // Ensure the song starts playing
                }
                updateUISong(currentSong, position);
            }
        });
    }

    private void loadBannerAd() {
        if (adContainer != null && !isNetworkConnected(PlayMusicActivity.this)) {
            adContainer.setVisibility(View.GONE);
            return;
        }

        // Call your method to show the ad, e.g., using AdMob or Facebook
        AdManager.showBanner(adContainer, shimmerFrameLayout, PlayMusicActivity.this);
    }


    public void onResume() {
        super.onResume();
        bindService(new Intent(this, MusicService.class), this, 1);
    }

    public void onPause() {
        super.onPause();
        MusicService musicService = this.musicService;
        if (musicService != null) {
            musicService.setCallBack(null);
        }
        Handler handler = this.handler;
        if (handler != null) {
            handler.removeCallbacks(this.runnable);
        }
        unbindService(this);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean booleanExtra = intent.getBooleanExtra(IntentExtra.EXTRA_FROM_NOTIFICATION, false);
        Log.d("aaa", "onNewIntent fromNotification = " + booleanExtra);
        if (booleanExtra) {
            this.mSongList.clear();
            ArrayList arrayList = (ArrayList) intent.getSerializableExtra(IntentExtra.EXTRA_MUSIC_ARRAY);
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    MusicInfo musicById = MusicDatabaseControl.getInstance().getMusicById(((Long) it.next()).longValue());
                    if (musicById != null) {
                        this.mSongList.add(musicById);
                    }
                }
            }
            MusicInfo musicInfo = (MusicInfo) intent.getSerializableExtra(IntentExtra.EXTRA_MUSIC_SONG);
            if (this.mSongList.isEmpty() && musicInfo != null) {
                this.mSongList.add(musicInfo);
            }
            this.mPosition = intent.getIntExtra(IntentExtra.EXTRA_MUSIC_NUMBER, 0);
        } else if (intent.getData() != null) {
            this.mSongList.clear();
            this.mSongList.add(MusicPlayerUtils.getMusicInfoFromUri(this, intent.getData()));
            this.mPosition = 0;
        }
        this.updateData = true;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void shareMusic(Context context, List<MusicInfo> musicInfo) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("audio/*");
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(musicInfo.get(this.mPosition).getPath())));
            intent.addFlags(1);
            context.startActivities(new Intent[]{Intent.createChooser(intent, "Share via")});
        } catch (Exception unused) {
            FirebaseAnalyticsUtils.putEventClick(context, "Log_error", "share_video_error");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        MusicService musicService;
        if (id == R.id.img_audio_effect_control) {
            openEqualizer();
        } else if (id == R.id.img_volume) {
            new ChangeVolumeBottomSheet().show(getSupportFragmentManager(), "Change volume");
        } else if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.iv_favorite) {
            boolean isActivated = !imgFavorite.isActivated();
            imgFavorite.setActivated(isActivated);
            int imageResId = isActivated ? R.drawable.ic_favorite_true : R.drawable.ic_favorite_false;
            imgFavorite.setImageResource(imageResId);
            MusicInfo currentSong = mSongList.get(mPosition);
            MusicFavoriteUtil.addFavoriteMusicId(this, currentSong.getId(), isActivated);
            musicService = this.musicService;
            if (musicService != null && musicService.getCurrentSong() != null) {
                MusicFavoriteUtil.addFavoriteMusicId(this, musicService.getCurrentSong().getId(), isActivated);
            }

        } else if (id == R.id.iv_lyric) {
            Toast.makeText(this, R.string.coming_soon, 0).show();
        } else if (id == R.id.iv_music_playlist) {
            if (!this.mSongList.isEmpty()) {
                if (this.mSongList.size() == 1) {
                    Toast.makeText(this, R.string.playlist_has_only_one_song, 0).show();
                    return;
                }
                MusicPlayerNextInDialogBuilder musicPlayerNextInDialogBuilder = new MusicPlayerNextInDialogBuilder(this, this.mSongList, this.mPosition, new Callback() {
                    public final void onMusicPlay(int i) {
                        PlayMusicActivity.this.m522xd8751a29(i);
                    }
                });
                this.musicBuilder = musicPlayerNextInDialogBuilder;
                musicPlayerNextInDialogBuilder.build().show();
            }
        } else if (id == R.id.img_btn_next) {
            musicService = this.musicService;
            if (musicService != null) {
                musicService.nextSong();
            }
        } else if (id == R.id.img_btn_play) {
            musicService = this.musicService;
            if (musicService != null) {
                musicService.playSong();
            }
        } else if (id == R.id.img_btn_previous) {
            musicService = this.musicService;
            if (musicService != null) {
                musicService.backSong();
            }
        } else if (id == R.id.img_repeat) {
            id = this.repeatState;
            if (id == 0) {
                this.repeatState = 1;
                this.imgRepeat.setImageResource(R.drawable.ic_repeat_one);
            } else if (id == 1) {
                this.repeatState = 2;
                this.imgRepeat.setImageResource(R.drawable.ic_repeat_all);
            } else {
                this.repeatState = 0;
                this.imgRepeat.setImageResource(R.drawable.ic_no_repeat);
            }
            musicService = this.musicService;
            if (musicService != null) {
                musicService.setRepeatState(this.repeatState);
            }
        } else if (id == R.id.img_shuffle) {
            MusicService musicService2 = this.musicService;
            boolean isSelected = imgShuffle.isSelected();
            imgShuffle.setSelected(!isSelected);
            imgShuffle.setImageResource(isSelected ? R.drawable.ic_music_shuffle_on : R.drawable.ic_music_shuffle_off);
            if (musicService2 != null) {
                musicService2.setShuffle(!isSelected);
            }
        }
    }

    public void m522xd8751a29(int i) {
        this.mPosition = i;
        if (i < this.mSongList.size()) {
            this.musicBuilder.updateCurrentSong(i);
        }
        MusicService musicService = this.musicService;
        if (musicService != null) {
            musicService.setIndex(i);
            this.musicService.playMusic();
        }
    }

    public void openEqualizer() {
        MusicService musicService = this.musicService;
        if (musicService != null) {
            new MusicPlayerEqualizerDialogBuilder(this, musicService.getSessionIdMusic()).build().show();
        }
    }

    private void startMediaController() {
        this.handler = new Handler();
        runOnUiThread(this.runnable);
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.musicService = ((MyBinder) iBinder).getService();
        Log.e("onServiceConnected ", this.musicService + "");
        if (!(this.musicService == null || this.mSongList.isEmpty())) {
            this.musicService.setCallBack(this);
            startMediaController();
            MusicInfo currentSong = this.musicService.getCurrentSong();
            if (currentSong != null) {
                if (this.updateData) {
                    this.musicService.setMusicInfoList(this.mSongList);
                    this.musicService.setIndex(this.mPosition);
                    this.updateData = false;
                }
                if (this.mPosition >= this.mSongList.size() || currentSong.equals(this.mSongList.get(this.mPosition))) {
                    this.tvTitle.setText(currentSong.getDisplayName());
                    this.tvSong.setText(currentSong.getDisplayName());
                    this.tvArtist.setText(currentSong.getArtist());
                    viewPagerMusicArt.setCurrentItem(mPosition);
                    this.imgFavorite.setActivated(MusicFavoriteUtil.checkFavoriteMusicIdExisted(this, mPosition));
                } else {
                    this.musicService.playMusic();
                }
            } else {
                this.musicService.setMusicInfoList(this.mSongList);
                this.musicService.setIndex(this.mPosition);
                this.musicService.setRepeatState(0);
                this.musicService.playMusic();
            }
            this.imgShuffle.setActivated(this.musicService.isShuffle());
            this.repeatState = this.musicService.getRepeatState();
            setRepeatIcon();
            if (this.musicService.isSongPlaying()) {
                this.btnPlayPause.setImageResource(R.drawable.ic_pause_music);
                return;
            }
            this.btnPlayPause.setImageResource(R.drawable.ic_play_music);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        Log.e("onServiceDisconnected ", this.musicService + "");
        MusicService musicService = this.musicService;
        if (musicService != null) {
            this.mPosition = musicService.getIndex();
        }
        this.musicService = null;
    }

    @Override
    public void updateUISong(MusicInfo musicInfo, int position) {
        mPosition = position;

        tvTitle.setText(musicInfo.getDisplayName());
        tvSong.setText(musicInfo.getDisplayName());
        tvArtist.setText(musicInfo.getArtist());
        viewPagerMusicArt.setCurrentItem(position, false);
        boolean isFavorited = MusicFavoriteUtil.checkFavoriteMusicIdExisted(this, musicInfo.getId());
        imgFavorite.setActivated(isFavorited);
        int imageResId = isFavorited ? R.drawable.ic_favorite_true : R.drawable.ic_favorite_false;
        imgFavorite.setImageResource(imageResId);
        btnPlayPause.setImageResource(R.drawable.ic_pause_music);
    }


    public void updatePlayPauseState(boolean z) {
        if (z) {
            this.btnPlayPause.setImageResource(R.drawable.ic_pause_music);
            ;
            return;
        }
        this.btnPlayPause.setImageResource(R.drawable.ic_play_music);
    }

    public void notifyError(Exception exception) {
        Toast.makeText(this, R.string.something_when_wrong, 0).show();
    }

    public void onStopService() {
        finish();
    }

    public void setRepeatIcon() {
        int i = this.repeatState;
        if (i == 1) {
            this.imgRepeat.setImageResource(R.drawable.ic_repeat_one);
        } else if (i == 2) {
            this.imgRepeat.setImageResource(R.drawable.ic_repeat_all);
        } else {
            this.imgRepeat.setImageResource(R.drawable.ic_no_repeat);
        }
    }
}
