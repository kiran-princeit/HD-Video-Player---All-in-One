package com.hdvideoplayer.smartplayer.player.activities;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.core.view.InputDeviceCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkRequest;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.IconModel;
import com.hdvideoplayer.smartplayer.player.BrightnessDialog;
import com.hdvideoplayer.smartplayer.player.OnGestureListener;
import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.TrackSelectionDialog;

import com.hdvideoplayer.smartplayer.player.VolumeDialog;
import com.hdvideoplayer.smartplayer.player.adapters.video.PlaybackiconsAdapter;
import com.hdvideoplayer.smartplayer.player.adapters.video.VideoHistoryAdapter;
import com.hdvideoplayer.smartplayer.player.adapters.video.VideoPlayerListAdapter;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoHistory;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.data.utils.VideoFavoriteUtil;
import com.hdvideoplayer.smartplayer.player.util.FirebaseAnalyticsUtils;
import com.hdvideoplayer.smartplayer.player.util.SharedPreferencesUtils;
import com.hdvideoplayer.smartplayer.player.util.constant.AppConstant;


public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, VideoHistoryAdapter.Callback, LifecycleObserver {
    AudioManager audioManager;
    public float baseX, baseY;
    public int brightness;
    ImageView brt_icon;
    ProgressBar brt_progress;
    LinearLayout brt_progress_container;
    TextView brt_text;
    LinearLayout brt_text_container;
    ImageView btn_next;
    ImageView btn_prev;
    boolean checkSeek = true;
    public ContentResolver contentResolver;
    private VideoPlayerActivity.ControlsMode controlsMode;
    boolean dark = false;
    public int device_height, device_width;
    public long diffX, diffY;
    public float distanceCovered = 0.0f;
    boolean doubleTap = false;
    RelativeLayout double_tap_play_pause;

    ImageView exo_forword;
    ImageView exo_rewind;
    boolean expand = false;
    public final ArrayList<IconModel> iconModelArrayList = new ArrayList<>();
    boolean isCrossChacked;
    public boolean isShowingTrackSelectionDialog;
    int lastWindowIndex = 0;
    boolean left;
    ImageView lock;
    public static List<VideoInfo> sVideoList = new ArrayList();
    public int media_volume;
    boolean mute = false;
    View nightmode;
    PictureInPictureParams.Builder pictureInpicture;
    PlaybackParameters playbackParameters;
    PlaybackiconsAdapter playbackiconsAdapter;
    float playbackspeed;
    SimpleExoPlayer player;
    PlayerView playerView;
    int position = 0;
    RecyclerView recyclerViewicons;
    boolean right;
    RelativeLayout rootlayoutvideo;
    ScaleGestureDetector scaleGestureDetector;
    private boolean isActivityResumed = false;
    RecyclerView recyclerView;
    RelativeLayout rl_videoOption;
    ImageView IVFavorite;

    public float scale_factor = 1.0f;
    View.OnClickListener scalefirstlistner = new View.OnClickListener() {
        public void onClick(View view) {
            VideoPlayerActivity.this.playerView.setResizeMode(3);
            VideoPlayerActivity.this.player.setVideoScalingMode(1);
            VideoPlayerActivity.this.scaling.setImageResource(R.drawable.fullscreen);
            VideoPlayerActivity.this.scaling.setOnClickListener(VideoPlayerActivity.this.scalesecondlistner);
        }
    };
    View.OnClickListener scalesecondlistner = new View.OnClickListener() {
        public void onClick(View view) {
            VideoPlayerActivity.this.playerView.setResizeMode(4);
            VideoPlayerActivity.this.player.setVideoScalingMode(1);
            VideoPlayerActivity.this.scaling.setImageResource(R.drawable.zoom);
            VideoPlayerActivity.this.scaling.setOnClickListener(VideoPlayerActivity.this.scalethirdlistner);
        }
    };
    View.OnClickListener scalethirdlistner = new View.OnClickListener() {
        public void onClick(View view) {
            VideoPlayerActivity.this.playerView.setResizeMode(0);
            VideoPlayerActivity.this.player.setVideoScalingMode(1);
            VideoPlayerActivity.this.scaling.setImageResource(R.drawable.fit);
            VideoPlayerActivity.this.scaling.setOnClickListener(VideoPlayerActivity.this.scalefirstlistner);
        }
    };
    ImageView scaling;

    public float seekdistance = 0.0f;
    boolean singleTap;
    boolean start = false;
    boolean success = false;
    boolean swipe_move = false;
    TextView title;
    ImageView unlock;
    boolean valueenable = true;
    ImageView videoBack;
    String videoTitle;
    ImageView video_more;
    long viewposition = 0;
    ImageView vol_icon;
    ProgressBar vol_progress;
    LinearLayout vol_progress_container;
    TextView vol_text;
    LinearLayout vol_text_container;
    public Window window;
    RelativeLayout zoomContainer;
    RelativeLayout zoomLayout;
    TextView zoom_perc;
    private VideoInfo currentVideo;
    private int currentWindowIndex;
    VideoPlayerListAdapter videoPlayerListAdapter;
    ImageView IVShare;
    private long mLastClickTime = 0;

    @Override
    public void onHistoryOptionSelect(VideoHistory videoHistory, int i, int i2) {

    }

    public enum ControlsMode {
        LOCK,
        FULLSCREEN
    }

    static float access(VideoPlayerActivity videoPlayerActivity, float f) {
        float f2 = videoPlayerActivity.scale_factor * f;
        videoPlayerActivity.scale_factor = f2;
        return f2;
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video_player_1);

        getLifecycle().addObserver(this);
        changeControl();
        initView();
        playVideo(currentVideo);
        swipeGesture();
        horizontalitemList();

        View overlayGuide = findViewById(R.id.overlay_guide);
        Button btnNext = findViewById(R.id.btn_next);

        LottieAnimationView fastForward = findViewById(R.id.lottie_fast_forward);
        LottieAnimationView rewind = findViewById(R.id.lottie_rewind);
        LottieAnimationView playPause = findViewById(R.id.lottie_play_pause);

        LottieAnimationView volume = findViewById(R.id.lottie_volume);
        LottieAnimationView brightness = findViewById(R.id.lottie_brightness);
        LottieAnimationView seekProgress = findViewById(R.id.lottie_seek_progress);

        // Set Lottie animations
        fastForward.setAnimation("lottie_fast_forward.json");
        rewind.setAnimation("lottie_rewind.json");
        playPause.setAnimation("lottie_pause_play.json");

        brightness.setAnimation("lottie_brightness.json");
        seekProgress.setAnimation("lottie_seek.json");
        volume.setAnimation("lottie_volume.json");

        // Show overlay only if it's the first time
        if (isFirstTime()) {
            overlayGuide.setVisibility(View.VISIBLE);
        } else {
            overlayGuide.setVisibility(View.GONE);
            markOverlayAsShown();
        }

        btnNext.setOnClickListener(v -> {
            overlayGuide.setVisibility(View.GONE);
            markOverlayAsShown();
        });
    }

    private boolean isFirstTime() {
        return getSharedPreferences("VideoPlayerPrefs", MODE_PRIVATE).getBoolean("overlay_shown", true);
    }

    private void markOverlayAsShown() {
        getSharedPreferences("VideoPlayerPrefs", MODE_PRIVATE).edit().putBoolean("overlay_shown", false).apply();
    }

    private void changeControl() {
        getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
    }

    public void horizontalitemList() {
        this.iconModelArrayList.add(new IconModel(R.drawable.ic_baseline_chevron_right_24, ""));
        this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_nights_stay_24, "Night"));
        this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_picture_in_picture_alt_24, "Popup"));
//        this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_equalizer_24, "Equalizer"));
        VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_brightness, "Brightness"));
        this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_crop_rotate_24, "Rotate"));
        this.playbackiconsAdapter = new PlaybackiconsAdapter(this.iconModelArrayList, this);
        this.recyclerViewicons.setLayoutManager(new LinearLayoutManager(this, 0, true));
        this.recyclerViewicons.setAdapter(this.playbackiconsAdapter);
        this.playbackiconsAdapter.notifyDataSetChanged();
        this.playbackiconsAdapter.setOnItemClickListener(new PlaybackiconsAdapter.OnItemClickListener() {
            public void OnItemClick(int i) {
                if (i == 0) {
                    if (VideoPlayerActivity.this.expand) {
                        VideoPlayerActivity.this.iconModelArrayList.clear();
                        VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_baseline_chevron_right_24, ""));
                        VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_nights_stay_24, "Night"));
                        VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_picture_in_picture_alt_24, "Popup"));
//                        VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_equalizer_24, "Equalizer"));
                        VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_brightness, "Brightness"));
                        VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_crop_rotate_24, "Rotate"));
                        VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                        VideoPlayerActivity.this.expand = false;
                    } else {
                        if (VideoPlayerActivity.this.iconModelArrayList.size() == 5) {
                            VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_volume_off_24, "Mute"));
                            VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_volume_up_24, "Valume"));
//                            VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_brightness, "Brightness"));
                            VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_round_fast_forward_24, "speed"));
                            VideoPlayerActivity.this.iconModelArrayList.add(new IconModel(R.drawable.ic_baseline_subtitles_24, "Subtitle"));
                        }
                        VideoPlayerActivity.this.iconModelArrayList.set(i, new IconModel(R.drawable.ic_round_keyboard_arrow_left_24, ""));
                        VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                        VideoPlayerActivity.this.expand = true;
                    }
                }
                if (i == 1) {
                    if (VideoPlayerActivity.this.dark) {
                        VideoPlayerActivity.this.nightmode.setVisibility(8);
                        VideoPlayerActivity.this.iconModelArrayList.set(i, new IconModel(R.drawable.ic_round_nights_stay_24, "Night"));
                        VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                        VideoPlayerActivity.this.dark = false;
                    } else {
                        VideoPlayerActivity.this.nightmode.setVisibility(0);
                        VideoPlayerActivity.this.iconModelArrayList.set(i, new IconModel(R.drawable.ic_round_nights_stay_24, "Day"));
                        VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                        VideoPlayerActivity.this.dark = true;
                    }
                }
                if (i == 2) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        VideoPlayerActivity.this.pictureInpicture.setAspectRatio(new Rational(16, 9));
                        VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
                        videoPlayerActivity.enterPictureInPictureMode(videoPlayerActivity.pictureInpicture.build());
                        recyclerView.setVisibility(View.GONE);
                        rl_videoOption.setVisibility(View.GONE);
                        title.setVisibility(View.GONE);
                    } else {
                        Log.wtf("not supported", "yes");
                    }
                }
                if (i == 4) {
                    if (VideoPlayerActivity.this.getResources().getConfiguration().orientation == 1) {
                        VideoPlayerActivity.this.hideBottomBar();
                        VideoPlayerActivity.this.getWindow().addFlags(1024);
                        VideoPlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                        hideViews();
                    } else if (VideoPlayerActivity.this.getResources().getConfiguration().orientation == 2) {
                        VideoPlayerActivity.this.showBottomBar();
                        VideoPlayerActivity.this.getWindow().clearFlags(1024);
                        VideoPlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                        showFullScreenMode();
                    }
                }
                if (i == 5) {
                    if (VideoPlayerActivity.this.mute) {
                        VideoPlayerActivity.this.player.setVolume(99.134f);
                        VideoPlayerActivity.this.iconModelArrayList.set(i, new IconModel(R.drawable.ic_round_volume_off_24, "Mute"));
                        VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                        VideoPlayerActivity.this.mute = false;
                    } else {
                        VideoPlayerActivity.this.player.setVolume(0.0f);
                        VideoPlayerActivity.this.iconModelArrayList.set(i, new IconModel(R.drawable.ic_round_volume_up_24, "Unmute"));
                        VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                        VideoPlayerActivity.this.mute = true;
                    }
                }
                if (i == 6) {
                    new VolumeDialog().show(VideoPlayerActivity.this.getSupportFragmentManager(), "dialog");
                    VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                }
                if (i == 3) {
                    new BrightnessDialog().show(VideoPlayerActivity.this.getSupportFragmentManager(), "dialog");
                    VideoPlayerActivity.this.playbackiconsAdapter.notifyDataSetChanged();
                }
                if (i == 7) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_playback_speed, null);
                    builder.setView(dialogView);
                    AppCompatButton okbtn = dialogView.findViewById(R.id.tv_dialog_ok);
                    TextView cancelbtn = dialogView.findViewById(R.id.tv_dialog_cancel);
                    AlertDialog playback = builder.create();
                    RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupPlaybackSpeed);
                    int selectedSpeed = -1;  // Default to -1 (no selection)

                    if (VideoPlayerActivity.this.playbackspeed == 0.25f) {
                        selectedSpeed = R.id.rb_025x;
                    } else if (VideoPlayerActivity.this.playbackspeed == 0.5f) {
                        selectedSpeed = R.id.rb_05x;
                    } else if (VideoPlayerActivity.this.playbackspeed == 1.0f) {
                        selectedSpeed = R.id.rb_1x;
                    } else if (VideoPlayerActivity.this.playbackspeed == 1.5f) {
                        selectedSpeed = R.id.rb_15x;
                    } else if (VideoPlayerActivity.this.playbackspeed == 2.0f) {
                        selectedSpeed = R.id.rb_2x;
                    }

                    if (selectedSpeed != -1) {
                        RadioButton selectedRadioButton = dialogView.findViewById(selectedSpeed);
                        selectedRadioButton.setChecked(true);
                    }

                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (checkedId == R.id.rb_025x) {
                                VideoPlayerActivity.this.playbackspeed = 0.25f;
                            } else if (checkedId == R.id.rb_05x) {
                                VideoPlayerActivity.this.playbackspeed = 0.5f;
                            } else if (checkedId == R.id.rb_1x) {
                                VideoPlayerActivity.this.playbackspeed = 1.0f;
                            } else if (checkedId == R.id.rb_15x) {
                                VideoPlayerActivity.this.playbackspeed = 1.5f;
                            } else if (checkedId == R.id.rb_2x) {
                                VideoPlayerActivity.this.playbackspeed = 2.0f;
                            }

                            // Set the playback speed
                            VideoPlayerActivity.this.playbackParameters = new PlaybackParameters(VideoPlayerActivity.this.playbackspeed);
                            VideoPlayerActivity.this.player.setPlaybackParameters(VideoPlayerActivity.this.playbackParameters);
                        }
                    });
                    okbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playback.cancel();
                        }
                    });

                    cancelbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playback.cancel();
                        }
                    });
                    playback.show();
                }

                if (i == 8 && !VideoPlayerActivity.this.isShowingTrackSelectionDialog && TrackSelectionDialog.hasValidTracks((Player) VideoPlayerActivity.this.player)) {
                    boolean unused = VideoPlayerActivity.this.isShowingTrackSelectionDialog = true;
                    TrackSelectionDialog.createForPlayer(VideoPlayerActivity.this.player, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            boolean unused = VideoPlayerActivity.this.isShowingTrackSelectionDialog = false;
                        }
                    }).show(VideoPlayerActivity.this.getSupportFragmentManager(), (String) null);
                }
            }
        });
    }

    public void initView() {
        this.IVShare = findViewById(R.id.IVShare);
        this.recyclerView = findViewById(R.id.recyclerView);
        this.rl_videoOption = findViewById(R.id.rl_videoOption);
        this.IVFavorite = findViewById(R.id.IVFavorite);
        this.nightmode = findViewById(R.id.nightmode);
        this.recyclerViewicons = (RecyclerView) findViewById(R.id.recycler_icon);
        this.btn_next = (ImageView) findViewById(R.id.exo_next);
        this.btn_prev = (ImageView) findViewById(R.id.exo_prev);
        this.video_more = (ImageView) findViewById(R.id.video_more);
        this.exo_rewind = (ImageView) findViewById(R.id.exo_rewind);
        this.exo_forword = (ImageView) findViewById(R.id.exo_forword);
        this.title = (TextView) findViewById(R.id.videotitle);
        this.playerView = (PlayerView) findViewById(R.id.exoplayer);
        this.vol_text = (TextView) findViewById(R.id.vol_text);
        this.brt_text = (TextView) findViewById(R.id.brt_text);
        this.vol_progress = (ProgressBar) findViewById(R.id.vol_progress);
        this.brt_progress = (ProgressBar) findViewById(R.id.brt_progress);
        this.vol_progress_container = (LinearLayout) findViewById(R.id.vol_progress_container);
        this.brt_progress_container = (LinearLayout) findViewById(R.id.brt_progress_container);
        this.vol_text_container = (LinearLayout) findViewById(R.id.vol_text_container);
        this.brt_text_container = (LinearLayout) findViewById(R.id.brt_text_container);
        this.vol_icon = (ImageView) findViewById(R.id.vol_icon);
        this.brt_icon = (ImageView) findViewById(R.id.brt_icon);
        this.videoBack = (ImageView) findViewById(R.id.ivBack);
        this.scaling = (ImageView) findViewById(R.id.exo_scalling);
        this.lock = (ImageView) findViewById(R.id.exo_lock);
        this.unlock = (ImageView) findViewById(R.id.exo_unlock);
        this.rootlayoutvideo = (RelativeLayout) findViewById(R.id.exo_root_layout);
        this.audioManager = (AudioManager) getSystemService("audio");
        this.zoomLayout = (RelativeLayout) findViewById(R.id.zoom_layout);
        this.zoom_perc = (TextView) findViewById(R.id.zoom_percentage);
        this.zoomContainer = (RelativeLayout) findViewById(R.id.zoom_container);
        this.double_tap_play_pause = (RelativeLayout) findViewById(R.id.double_tap_play_pause);
        this.scaleGestureDetector = new ScaleGestureDetector(this, new VideoPlayerActivity.ScaleDetector());
        this.btn_next.setOnClickListener(this);
        this.btn_prev.setOnClickListener(this);
        this.exo_forword.setOnClickListener(this);
        this.exo_rewind.setOnClickListener(this);
        this.videoBack.setOnClickListener(this);
        this.lock.setOnClickListener(this);
        this.unlock.setOnClickListener(this);
        this.scaling.setOnClickListener(this.scalefirstlistner);
        this.video_more.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(VideoPlayerActivity.this, view);
                popupMenu.setOnMenuItemClickListener(VideoPlayerActivity.this);
                popupMenu.inflate(R.menu.video_menu);
                popupMenu.show();
            }
        });

        position = getIntent().getIntExtra(AppConstant.IntentExtra.EXTRA_VIDEO_NUMBER, 0);
        this.currentWindowIndex = position;

        if (sVideoList == null || sVideoList.isEmpty() || position >= sVideoList.size()) {
            Toast.makeText(this, "No video available to play", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            this.currentVideo = sVideoList.get(this.currentWindowIndex);
        }


        this.viewposition = getIntent().getLongExtra("viewposition", 0);

        this.videoTitle = currentVideo.getDisplayName();
        this.title.setText(this.videoTitle);
        if (Build.VERSION.SDK_INT >= 26) {
            this.pictureInpicture = new PictureInPictureParams.Builder();
        }

        Log.e("videoplyAct", "position: " + position);
        Log.e("videoplyAct", "currentVideo: " + currentVideo);
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        IVFavorite.startAnimation(animShake);

        IVFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IVFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isActivated = !IVFavorite.isActivated();
                        IVFavorite.setActivated(isActivated);
                        int imageResId = isActivated ? R.drawable.ic_favorite_true : R.drawable.ic_favorite_false;
                        IVFavorite.setImageResource(imageResId);
                        VideoFavoriteUtil.addFavoriteVideoId(VideoPlayerActivity.this, currentVideo.getId(), isActivated);
                    }
                });

            }
        });
        IVShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                shareVideo(VideoPlayerActivity.this, sVideoList);
            }
        });

        videoPlayerListAdapter = new VideoPlayerListAdapter(VideoPlayerActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(videoPlayerListAdapter);
        saveVideoToHistory(currentVideo, position);

        List<VideoInfo> videoList = getVideoList(); // Replace with your actual video list loading logic
        videoPlayerListAdapter.updateVideoDataList(videoList);


    }

    public void shareVideo(Context context, List<VideoInfo> videoInfos) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");

            // Get the video URI using FileProvider
            Uri videoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(videoInfos.get(this.position).getPath()));

            // Grant permission to read the URI
            intent.putExtra(Intent.EXTRA_STREAM, videoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Ensure the receiving app can access the file

            // Create chooser for the user to select the app
            context.startActivity(Intent.createChooser(intent, "Share via"));

        } catch (Exception e) {
            e.printStackTrace();
            FirebaseAnalyticsUtils.putEventClick(context, "Log_error", "share_video_error");
        }
    }

    private List<VideoInfo> getVideoList() {
        List<VideoInfo> videoList = new ArrayList<>();

        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION
        };
        String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"; // Sort by the most recent
        Cursor cursor = getContentResolver().query(videoUri, projection, null, null, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setPath(videoPath);
                videoInfo.setDisplayName(displayName);
                videoInfo.setDuration(duration);
                videoInfo.setDate((dateAdded) * 1000);
                videoInfo.setSize(size);
                videoList.add(videoInfo);
            }
            cursor.close();
        }

        return videoList;
    }

    private void saveVideoToHistory(VideoInfo video, long currentPosition) {
        long currentTime = System.currentTimeMillis();
        VideoHistory videoHistory = new VideoHistory();
        videoHistory.setVideo(video);
        videoHistory.setCurrentPosition(currentPosition);
        videoHistory.setDateAdded(currentTime);
        videoHistory.setId(video.getId());
        List<VideoHistory> videoHistoryList = SharedPreferencesUtils.getHistoryList(this, "video_history_key");
        videoHistoryList.add(0, videoHistory);
        SharedPreferencesUtils.putHistoryList(this, "video_history_key", videoHistoryList);
    }

    public void swipeGesture() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.device_width = displayMetrics.widthPixels;
        this.device_height = displayMetrics.heightPixels;
        this.playerView.setOnTouchListener(new OnGestureListener(this) {
            public void onHorizontalScroll(MotionEvent motionEvent, float f) {
            }

            public void onSwipeBottom() {
            }

            public void onSwipeTop() {
            }

            public void onTap() {
            }

            public void onVerticalScroll(MotionEvent motionEvent, float f) {
            }

            public boolean onTouch(View view, MotionEvent motionEvent) {
                MotionEvent motionEvent2 = motionEvent;
                int action = motionEvent.getAction();
                if (action == 0) {
                    float unused = VideoPlayerActivity.this.seekdistance = 0.0f;
                    float unused2 = VideoPlayerActivity.this.distanceCovered = 0.0f;
                    VideoPlayerActivity.this.playerView.showController();
                    VideoPlayerActivity.this.start = true;
                    if (motionEvent.getX() < ((float) (VideoPlayerActivity.this.device_width / 2))) {
                        VideoPlayerActivity.this.left = true;
                        VideoPlayerActivity.this.right = false;
                    } else if (motionEvent.getX() > ((float) (VideoPlayerActivity.this.device_width / 2))) {
                        VideoPlayerActivity.this.left = false;
                        VideoPlayerActivity.this.right = true;
                    }
                    float unused3 = VideoPlayerActivity.this.baseX = motionEvent.getX();
                    float unused4 = VideoPlayerActivity.this.baseY = motionEvent.getY();
                } else if (action == 1) {
                    VideoPlayerActivity.this.swipe_move = false;
                    VideoPlayerActivity.this.start = false;
                    VideoPlayerActivity.this.vol_progress_container.setVisibility(8);
                    VideoPlayerActivity.this.brt_progress_container.setVisibility(8);
                    VideoPlayerActivity.this.vol_text_container.setVisibility(8);
                    VideoPlayerActivity.this.brt_text_container.setVisibility(8);
                } else if (action == 2) {
                    VideoPlayerActivity.this.swipe_move = true;
                    long unused5 = VideoPlayerActivity.this.diffX = (long) Math.ceil((double) (motionEvent.getX() - VideoPlayerActivity.this.baseX));
                    long unused6 = VideoPlayerActivity.this.diffY = (long) Math.ceil((double) (motionEvent.getY() - VideoPlayerActivity.this.baseY));
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
                    float unused7 = videoPlayerActivity.distanceCovered = videoPlayerActivity.getDistance(x, y, motionEvent2);
                    try {
                        if (VideoPlayerActivity.this.checkSeek) {
                            VideoPlayerActivity.this.changeSeek(motionEvent2.getHistoricalX(0, 0), motionEvent2.getHistoricalY(0, 0), x, y, VideoPlayerActivity.this.distanceCovered, "X");
                        }
                    } catch (IllegalArgumentException unused8) {
                    }
                    if (Math.abs(VideoPlayerActivity.this.diffY) > 100) {
                        VideoPlayerActivity.this.start = true;
                        if (Math.abs(VideoPlayerActivity.this.diffY) > Math.abs(VideoPlayerActivity.this.diffX)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Settings.System.canWrite(VideoPlayerActivity.this.getApplicationContext())) {
                                    if (VideoPlayerActivity.this.valueenable) {
                                        if (VideoPlayerActivity.this.left) {
                                            VideoPlayerActivity videoPlayerActivity2 = VideoPlayerActivity.this;
                                            ContentResolver unused9 = videoPlayerActivity2.contentResolver = videoPlayerActivity2.getContentResolver();
                                            VideoPlayerActivity videoPlayerActivity3 = VideoPlayerActivity.this;
                                            Window unused10 = videoPlayerActivity3.window = videoPlayerActivity3.getWindow();
                                            try {
                                                Settings.System.putInt(VideoPlayerActivity.this.contentResolver, "screen_brightness_mode", 0);
                                                VideoPlayerActivity videoPlayerActivity4 = VideoPlayerActivity.this;
                                                int unused11 = videoPlayerActivity4.brightness = Settings.System.getInt(videoPlayerActivity4.contentResolver, "screen_brightness");
                                            } catch (Settings.SettingNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            int access$1200 = (int) (((double) VideoPlayerActivity.this.brightness) - (((double) VideoPlayerActivity.this.diffY) * 0.01d));
                                            if (access$1200 > 250) {
                                                access$1200 = 250;
                                            } else if (access$1200 < 1) {
                                                access$1200 = 1;
                                            }
                                            double ceil = Math.ceil((((double) access$1200) / 250.0d) * 100.0d);
                                            VideoPlayerActivity.this.brt_progress_container.setVisibility(0);
                                            VideoPlayerActivity.this.brt_text_container.setVisibility(0);
                                            int i = (int) ceil;
                                            VideoPlayerActivity.this.brt_progress.setProgress(i);
                                            if (ceil < 30.0d) {
                                                VideoPlayerActivity.this.brt_icon.setImageResource(R.drawable.ic_brightness_low);
                                            } else if (ceil > 30.0d && ceil < 80.0d) {
                                                VideoPlayerActivity.this.brt_icon.setImageResource(R.drawable.ic_brightness_medium);
                                            } else if (ceil > 80.0d) {
                                                VideoPlayerActivity.this.brt_icon.setImageResource(R.drawable.ic_brightness);
                                            }
                                            VideoPlayerActivity.this.brt_text.setText(" " + i + "%");
                                            Settings.System.putInt(VideoPlayerActivity.this.contentResolver, "screen_brightness", access$1200);
                                            WindowManager.LayoutParams attributes = VideoPlayerActivity.this.window.getAttributes();
                                            attributes.screenBrightness = ((float) VideoPlayerActivity.this.brightness) / 255.0f;
                                            VideoPlayerActivity.this.window.setAttributes(attributes);
                                        } else if (VideoPlayerActivity.this.right) {
                                            VideoPlayerActivity.this.vol_text_container.setVisibility(0);
                                            VideoPlayerActivity videoPlayerActivity5 = VideoPlayerActivity.this;
                                            int unused12 = videoPlayerActivity5.media_volume = videoPlayerActivity5.audioManager.getStreamVolume(3);
                                            int streamMaxVolume = VideoPlayerActivity.this.audioManager.getStreamMaxVolume(3);
                                            double d = (double) streamMaxVolume;
                                            int access$1300 = VideoPlayerActivity.this.media_volume - ((int) (((double) VideoPlayerActivity.this.diffY) * (d / (((double) (VideoPlayerActivity.this.device_height * 2)) - 0.5d))));
                                            if (access$1300 <= streamMaxVolume) {
                                                streamMaxVolume = access$1300 < 1 ? 0 : access$1300;
                                            }
                                            VideoPlayerActivity.this.audioManager.setStreamVolume(3, streamMaxVolume, 8);
                                            double ceil2 = Math.ceil((((double) streamMaxVolume) / d) * 100.0d);
                                            int i2 = (int) ceil2;
                                            VideoPlayerActivity.this.vol_text.setText(" " + i2 + "%");
                                            if (ceil2 < 1.0d) {
                                                VideoPlayerActivity.this.vol_icon.setImageResource(R.drawable.ic_volume_off);
                                                VideoPlayerActivity.this.vol_text.setVisibility(0);
                                                VideoPlayerActivity.this.vol_text.setText("Off");
                                            } else if (ceil2 >= 1.0d) {
                                                VideoPlayerActivity.this.vol_icon.setImageResource(R.drawable.ic_volume_up);
                                                VideoPlayerActivity.this.vol_text.setVisibility(0);
                                            }
                                            VideoPlayerActivity.this.vol_progress_container.setVisibility(0);
                                            VideoPlayerActivity.this.vol_progress.setProgress(i2);
                                        }
                                        VideoPlayerActivity.this.success = true;
                                    }
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (Settings.System.canWrite(VideoPlayerActivity.this)) {
                                        Toast.makeText(VideoPlayerActivity.this, "Granted", 0).show();
                                    } else {

//                                        Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
//                                        intent.setData(Uri.parse("package:" + VideoPlayerActivity.this.getPackageName()));
//                                        VideoPlayerActivity.this.startActivityForResult(intent, 111);
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getPackageName()));
                                        if (intent.resolveActivity(getPackageManager()) != null) {
                                            VideoPlayerActivity.this.startActivityForResult(intent, 111);
                                        } else {
                                            Toast.makeText(VideoPlayerActivity.this, "Unable to open settings. Please grant permissions manually.", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                VideoPlayerActivity.this.scaleGestureDetector.onTouchEvent(motionEvent2);
                return super.onTouch(view, motionEvent);
            }

            public void onSingleClick() {
                super.onSingleClick();
                if (VideoPlayerActivity.this.singleTap) {
                    VideoPlayerActivity.this.playerView.showController();
                    VideoPlayerActivity.this.singleTap = false;
                } else {
                    VideoPlayerActivity.this.playerView.hideController();
                    VideoPlayerActivity.this.singleTap = true;
                }
                if (VideoPlayerActivity.this.double_tap_play_pause.getVisibility() == 0) {
                    VideoPlayerActivity.this.double_tap_play_pause.setVisibility(8);
                }
            }

            public void onDoubleClick() {
                super.onDoubleClick();
                if (VideoPlayerActivity.this.doubleTap) {
                    VideoPlayerActivity.this.onResume();
                    VideoPlayerActivity.this.double_tap_play_pause.setVisibility(8);
                    VideoPlayerActivity.this.doubleTap = false;
                    return;
                }
                VideoPlayerActivity.this.player.setPlayWhenReady(false);
                VideoPlayerActivity.this.double_tap_play_pause.setVisibility(0);
                VideoPlayerActivity.this.doubleTap = true;
            }

            public void onSwipeRight() {
                super.onSwipeRight();
            }

            public void onSwipeLeft() {
                super.onSwipeLeft();
            }
        });
    }

    public void changeSeek(float f, float f2, float f3, float f4, float f5, String str) {
        if (str == "Y" && f3 == f) {
            float f6 = f5 / 300.0f;
            if (f4 < f2) {
                seekCommon(f6);
            } else {
                seekCommon(-f6);
            }
        } else if (str == "X" && f4 == f2) {
            float f7 = f5 / 200.0f;
            if (f3 > f) {
                seekCommon(f7);
            } else {
                seekCommon(-f7);
            }
        }
    }

    public void seekCommon(float f) {
        float f2 = f * 60000.0f;
        this.seekdistance += f2;
        if (this.player != null) {
            long j = (long) ((int) f2);
            Log.e("after", (this.player.getCurrentPosition() + j) + "");
            Log.e("seek distance", ((int) this.seekdistance) + "");
            if (this.player.getCurrentPosition() + j > 0 && this.player.getCurrentPosition() + j < this.player.getDuration() + 10) {
                SimpleExoPlayer simpleExoPlayer = this.player;
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + j);
            }
        }
    }

    public float getDistance(float f, float f2, MotionEvent motionEvent) {
        int historySize = motionEvent.getHistorySize();
        float f3 = 0.0f;
        int i = 0;
        while (i < historySize) {
            float historicalX = motionEvent.getHistoricalX(0, i);
            float historicalY = motionEvent.getHistoricalY(0, i);
            float f4 = historicalX - f;
            float f5 = historicalY - f2;
            f3 = (float) (((double) f3) + Math.sqrt((double) ((f4 * f4) + (f5 * f5))));
            i++;
            f = historicalX;
            f2 = historicalY;
        }
        float x = motionEvent.getX(0) - f;
        float y = motionEvent.getY(0) - f2;
        return (float) (((double) f3) + Math.sqrt((double) ((x * x) + (y * y))));
    }

    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.exo_play_speed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_playback_speed, null);
            builder.setView(dialogView);
            AppCompatButton okbtn = dialogView.findViewById(R.id.tv_dialog_ok);
            TextView cancelbtn = dialogView.findViewById(R.id.tv_dialog_cancel);
            AlertDialog playbackspeed = builder.create();
            RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupPlaybackSpeed);
            int selectedSpeed = -1;  // Default to -1 (no selection)

            if (VideoPlayerActivity.this.playbackspeed == 0.25f) {
                selectedSpeed = R.id.rb_025x;
            } else if (VideoPlayerActivity.this.playbackspeed == 0.5f) {
                selectedSpeed = R.id.rb_05x;
            } else if (VideoPlayerActivity.this.playbackspeed == 1.0f) {
                selectedSpeed = R.id.rb_1x;
            } else if (VideoPlayerActivity.this.playbackspeed == 1.5f) {
                selectedSpeed = R.id.rb_15x;
            } else if (VideoPlayerActivity.this.playbackspeed == 2.0f) {
                selectedSpeed = R.id.rb_2x;
            }

            if (selectedSpeed != -1) {
                RadioButton selectedRadioButton = dialogView.findViewById(selectedSpeed);
                selectedRadioButton.setChecked(true);
            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.rb_025x) {
                        VideoPlayerActivity.this.playbackspeed = 0.25f;
                    } else if (checkedId == R.id.rb_05x) {
                        VideoPlayerActivity.this.playbackspeed = 0.5f;
                    } else if (checkedId == R.id.rb_1x) {
                        VideoPlayerActivity.this.playbackspeed = 1.0f;
                    } else if (checkedId == R.id.rb_15x) {
                        VideoPlayerActivity.this.playbackspeed = 1.5f;
                    } else if (checkedId == R.id.rb_2x) {
                        VideoPlayerActivity.this.playbackspeed = 2.0f;
                    }

                    // Set the playback speed
                    VideoPlayerActivity.this.playbackParameters = new PlaybackParameters(VideoPlayerActivity.this.playbackspeed);
                    VideoPlayerActivity.this.player.setPlaybackParameters(VideoPlayerActivity.this.playbackParameters);
                }
            });
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playbackspeed.cancel();
                }
            });

            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playbackspeed.cancel();
                }
            });
            playbackspeed.show();

            return true;
        } else if (menuItem.getItemId() == R.id.exo_fullscreen) {
            if (getResources().getConfiguration().orientation == 2) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                showFullScreenMode();
            } else if (getResources().getConfiguration().orientation == 1) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                hideViews();
            }
            return true;
        } else {
            if (menuItem.getItemId() == R.id.exo_track_selection && !this.isShowingTrackSelectionDialog && TrackSelectionDialog.hasValidTracks((Player) this.player)) {
                this.isShowingTrackSelectionDialog = true;
                TrackSelectionDialog.createForPlayer(this.player, new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        isShowingTrackSelectionDialog = false;
                    }
                }).show(getSupportFragmentManager(), (String) null);
            }
            return false;
        }
    }

    public void hideBottomBar() {
        getWindow().getDecorView().setSystemUiVisibility(InputDeviceCompat.SOURCE_TOUCHSCREEN);
    }

    public void showBottomBar() {
        getWindow().getDecorView().setSystemUiVisibility(6144);
    }


    @SuppressLint("RestrictedApi")
    public void playVideo(VideoInfo selectedVideo) {
        if (selectedVideo == null) return;
        try {
            // Avoid reinitializing the player if it already exists
            if (this.player == null) {
                this.player = new SimpleExoPlayer.Builder(this)
                        .setLoadControl(
                                new DefaultLoadControl.Builder()
                                        .setAllocator(new DefaultAllocator(true, 16))
                                        .setBufferDurationsMs(5000, 15000, 2500, 5000)
                                        .setTargetBufferBytes(-1)
                                        .setPrioritizeTimeOverSizeThresholds(true)
                                        .createDefaultLoadControl()
                        ).build();

                // Attach the player to the PlayerView
                this.playerView.setPlayer(this.player);
                this.playerView.setKeepScreenOn(true);
            }

            // Create a new ConcatenatingMediaSource
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"));
            ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();

            // Add all videos from the list to the concatenated source
            for (VideoInfo video : sVideoList) {

                Uri videoUri = Uri.parse(selectedVideo.getPath());
//                                Uri videoUri = Uri.parse(video.getPath());
                MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(videoUri));
                concatenatingMediaSource.addMediaSource(mediaSource);
            }


            // Prepare the player with the concatenated media source
            this.player.setMediaSource(concatenatingMediaSource);
            this.player.prepare();

            // Play the selected video based on its position
            this.player.seekTo(this.position, this.viewposition);


//            this.playerView.setPlayer(this.player);
//            this.playerView.setKeepScreenOn(true);
//            this.player.prepare(concatenatingMediaSource);
//            try {
//                this.player.seekTo(this.position, this.viewposition);
//            } catch (Exception unused2) {
//                this.player.seekTo(0);
//            }


            boolean isFavorited = VideoFavoriteUtil.checkFavoriteVideoIdExisted(this, currentVideo.getId());
            IVFavorite.setActivated(isFavorited);
            int imageResId = isFavorited ? R.drawable.ic_favorite_true : R.drawable.ic_favorite_false;
            IVFavorite.setImageResource(imageResId);


//            Uri data = getIntent().getData();
//            if (data == null) {
//                data = (Uri) getIntent().getParcelableExtra("android.intent.extra.STREAM");
//            }
//            if (data != null) {
//                this.title.setText(getFileName(data));
//                concatenatingMediaSource.addMediaSource(new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(data)));
//            }

        } catch (Exception e) {
            Log.e("VideoPlayer", "Error playing video: " + e.getMessage(), e);
            playerError();
        }
    }

    private void playerError() {
        this.player.addListener(new Player.Listener() {
            public void onPlayerError(PlaybackException playbackException) {
                Player.Listener.super.onPlayerError(playbackException);
                Toast.makeText(VideoPlayerActivity.this, "Video Playing Error" + playbackException, 0).show();
            }

            public void onPlayerStateChanged(boolean z, int i) {
                int currentWindowIndex = VideoPlayerActivity.this.player.getCurrentWindowIndex();
                if (currentWindowIndex != VideoPlayerActivity.this.lastWindowIndex) {
                    VideoPlayerActivity.this.lastWindowIndex = currentWindowIndex;
                    VideoPlayerActivity.this.title.setText(VideoPlayerActivity.this.sVideoList.get(VideoPlayerActivity.this.lastWindowIndex).getDisplayName());
                }
            }

            public void onMediaItemTransition(MediaItem mediaItem, int i) {
                int currentWindowIndex = VideoPlayerActivity.this.player.getCurrentWindowIndex();
                if (currentWindowIndex != VideoPlayerActivity.this.lastWindowIndex) {
                    VideoPlayerActivity.this.lastWindowIndex = currentWindowIndex;
                    VideoPlayerActivity.this.title.setText(VideoPlayerActivity.this.sVideoList.get(VideoPlayerActivity.this.lastWindowIndex).getDisplayName());
                }
            }
        });
        this.player.setPlayWhenReady(true);
    }

    public void onPause() {
        isActivityResumed = false;
        super.onPause();
        this.player.setPlayWhenReady(false);
        this.player.getPlaybackState();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInPictureInPictureMode()) {
                this.player.setPlayWhenReady(true);
                return;
            }
        }
        this.player.setPlayWhenReady(false);
        this.player.getPlaybackState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release resources
    }

    public void onResume() {
        super.onResume();
        isActivityResumed = true;
        this.player.setPlayWhenReady(true);
        this.player.getPlaybackState();
    }

    public void onRestart() {
        super.onRestart();
        this.player.setPlayWhenReady(true);
        this.player.getPlaybackState();
    }

    public void lockDeviceRotation(boolean z) {
        if (z) {
            int i = getResources().getConfiguration().orientation;
            if (i == 2) {
                setRequestedOrientation(6);
            } else {
                setRequestedOrientation(7);
            }
            if (i == 1) {
                setRequestedOrientation(7);
            } else {
                setRequestedOrientation(6);
            }
        } else {
            getWindow().clearFlags(16);
            int i2 = getResources().getConfiguration().orientation;
            if (i2 == 2) {
                setRequestedOrientation(6);
            } else {
                setRequestedOrientation(7);
            }
            if (i2 == 1) {
                setRequestedOrientation(7);
            } else {
                setRequestedOrientation(6);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivBack) {
            onBackPressed();
        } else if (view.getId() == R.id.exo_lock) {
            this.checkSeek = true;
            this.valueenable = true;
            this.controlsMode = VideoPlayerActivity.ControlsMode.FULLSCREEN;
            this.rootlayoutvideo.setVisibility(View.VISIBLE);
            this.lock.setVisibility(View.GONE);
            lockDeviceRotation(false);
            Toast.makeText(this, "Unlocked", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.exo_unlock) {
            this.checkSeek = false;
            this.valueenable = false;
            this.controlsMode = VideoPlayerActivity.ControlsMode.LOCK;
            this.rootlayoutvideo.setVisibility(View.GONE);
            this.lock.setVisibility(View.VISIBLE);
            lockDeviceRotation(true);
            Toast.makeText(this, "Locked", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.exo_next) {
            if (position < sVideoList.size() - 1) {
                try {
                    Log.d("VideoPlayer11", "Current position: " + position);
                    position++;
                    currentVideo = sVideoList.get(position);

                    Log.d("VideoPlayer11", "Playing video at position: " + position);
                    playVideo(currentVideo);

                    videoTitle = currentVideo.getDisplayName();
                    title.setText(videoTitle);
                    Log.d("VideoPlayer11", "Video title updated: " + videoTitle);
                } catch (Exception e) {
                    Log.e("VideoPlayer11", "Error playing next video: " + e.getMessage(), e);
                    Toast.makeText(this, "Error playing next video", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No next video", Toast.LENGTH_SHORT).show();
                Log.d("VideoPlayer11", "No next video available");
            }

        } else if (view.getId() == R.id.exo_prev) {
            if (position > 0) {
                try {
                    this.position--;
                    this.currentVideo = sVideoList.get(this.position); // Update current video
                    this.videoTitle = this.currentVideo.getDisplayName(); // Get title
                    this.title.setText(this.videoTitle); // Update UI
                    playVideo(this.currentVideo); // Play selected video
                } catch (Exception e) {
                    Toast.makeText(this, "Error playing previous video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No previous video", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.exo_rewind) {
            try {
                this.player.seekTo(this.player.getCurrentPosition() - WorkRequest.MIN_BACKOFF_MILLIS); // Rewind 10 seconds
            } catch (Exception e) {
                Toast.makeText(this, "Error rewinding: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.exo_forword) {
            try {
                this.player.seekTo(this.player.getCurrentPosition() + WorkRequest.MIN_BACKOFF_MILLIS); // Fast forward 10 seconds
            } catch (Exception e) {
                Toast.makeText(this, "Error forwarding: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 111) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(getApplicationContext())) {
                this.success = true;
            } else {
                Toast.makeText(this, "Not Granted", 0).show();
            }
        }
    }

    private class ScaleDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleDetector() {
        }

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            VideoPlayerActivity.access(VideoPlayerActivity.this, scaleGestureDetector.getScaleFactor());
            VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
            float unused = videoPlayerActivity.scale_factor = Math.max(0.5f, Math.min(videoPlayerActivity.scale_factor, 6.0f));
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            VideoPlayerActivity.this.zoomContainer.setVisibility(8);
            super.onScaleEnd(scaleGestureDetector);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean z, Configuration configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(z, configuration);
        }
        this.isCrossChacked = z;
        if (z) {
            hideViews();
            this.playerView.hideController();
        } else {
            showFullScreenMode();
            this.playerView.showController();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.isCrossChacked) {
            this.player.release();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode();
            hideViews();
        } else {
            super.onBackPressed();
            showFullScreenMode();
//            SimpleExoPlayer simpleExoPlayer = this.player;
//            if (simpleExoPlayer != null) {
//                simpleExoPlayer.release();
//            }
        }
    }

    private void hideViews() {
        recyclerView.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        rl_videoOption.setVisibility(View.GONE);
    }

    private void showFullScreenMode() {
        recyclerView.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        rl_videoOption.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onUserLeaveHint() {
        // Trigger PIP mode when the user presses the home button
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isVideoPlaying()) {
            enterPictureInPictureMode();
        }
        super.onUserLeaveHint();
    }

    private boolean isVideoPlaying() {
        if (player != null) {
            return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
        }
        return false;
    }

//    public void enterPictureInPictureMode() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Define the aspect ratio for the PIP window
//            Rational aspectRatio = new Rational(16, 9); // Adjust to your video aspect ratio
//            PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
//            pipBuilder.setAspectRatio(aspectRatio);
//
//            // Enter PIP mode
//            enterPictureInPictureMode(pipBuilder.build());
//        }
//    }


    public void enterPictureInPictureMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                Rational aspectRatio = new Rational(16, 9);
                PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
                pipBuilder.setAspectRatio(aspectRatio);

                // Enter PIP mode
                enterPictureInPictureMode(pipBuilder.build());
            } else {
                Log.w("PiP", "Device does not support Picture-in-Picture mode");
            }
        }
    }


}
