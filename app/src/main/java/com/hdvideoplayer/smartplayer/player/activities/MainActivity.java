package com.hdvideoplayer.smartplayer.player.activities;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.facebook.shimmer.ShimmerFrameLayout;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adsprosimple.AdManager;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.fragments.SettingFragment;
import com.hdvideoplayer.smartplayer.player.fragments.music.MusicManagerFragment;
import com.hdvideoplayer.smartplayer.player.fragments.video.VideoManagerFragment;
import com.hdvideoplayer.smartplayer.player.services.MusicService;
import com.hdvideoplayer.smartplayer.player.util.music.MusicPlayerUtils;

import java.util.ArrayList;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.util.constant.AppConstant;

public class MainActivity extends BaseActivity implements ServiceConnection {
    private MusicInfo currentMusic;
    Handler handler = new Handler();
    private boolean isBindService = false;
    private boolean isPlaying = false;
    private boolean isVideoPlaying = false; // Track video playback state
    private boolean isRunning = false;
    private ImageView ivPlay;
    int version;
    private ImageView ivThumbnailMusic;
    private RelativeLayout layoutCurrentMusic;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MusicInfo musicInfo = (MusicInfo) intent.getSerializableExtra(AppConstant.IntentExtra.EXTRA_MUSIC_SONG);
            MainActivity.this.isPlaying = intent.getBooleanExtra(AppConstant.IntentExtra.EXTRA_MUSIC_PLAYING, false);
            MainActivity.this.position = intent.getIntExtra(AppConstant.IntentExtra.EXTRA_MUSIC_NUMBER, 0);
            ArrayList arrayList = (ArrayList) intent.getSerializableExtra(AppConstant.IntentExtra.EXTRA_MUSIC_ARRAY);
            if (!(arrayList == null || arrayList.isEmpty())) {
                MainActivity.this.musicIdList.clear();
                MainActivity.this.musicIdList.addAll(arrayList);
            }
            String str = " ";
            Log.d("aaa", " onReceive = " + MainActivity.this.isPlaying + str + MainActivity.this.position + str + MainActivity.this.isRunning + str + MainActivity.this.isBindService + str + musicInfo);
            if (MainActivity.this.isRunning) {
                if (musicInfo == null) {
                    if (MainActivity.this.isBindService) {
                        MainActivity.this.isBindService = false;
                        if (MainActivity.this.handler != null) {
                            MainActivity.this.handler.removeCallbacks(MainActivity.this.runnable);
                        }
                        MainActivity mainActivity = MainActivity.this;
                        mainActivity.unbindService(mainActivity);
                    }
                } else if (!MainActivity.this.isBindService) {
                    intent = new Intent(MainActivity.this, MusicService.class);
                    MainActivity mainActivity2 = MainActivity.this;
                    mainActivity2.isBindService = mainActivity2.bindService(intent, mainActivity2, 1);
                }
                if (MainActivity.this.currentMusic == null || !MainActivity.this.currentMusic.equals(musicInfo)) {
                    MainActivity.this.currentMusic = musicInfo;
                    MainActivity.this.setCurrentMusicContent();
                } else {
                    MainActivity.this.ivPlay.setImageResource(MainActivity.this.isPlaying ? R.drawable.ic_noti_pause : R.drawable.ic_noti_play);
                    MainActivity.this.layoutCurrentMusic.setVisibility(0);
                }
            }
            MainActivity.this.currentMusic = musicInfo;
        }
    };

    private static final int UPDATE_REQUEST_CODE = 17362;
    private AppUpdateManager appUpdateManager;
    private final List<Long> musicIdList = new ArrayList();
    private MusicService musicService;
    private int position;
    private final Runnable runnable = new Runnable() {
        public void run() {
            if (MainActivity.this.musicService != null) {
                int currentSeek = MainActivity.this.musicService.getCurrentSeek();
                int totalDuration = MainActivity.this.musicService.getTotalDuration();
                MainActivity.this.timeControl.setProgress(currentSeek);
                MainActivity.this.timeControl.setMax(totalDuration);
                if (MainActivity.this.handler != null) {
                    MainActivity.this.handler.postDelayed(this, 500);
                }
            }
        }
    };

    private ProgressBar timeControl;
    private TextView tvArtist;
    ImageView iv_icon;
    private AlertDialog infodialog;
    private TextView tvSong, tv_video, tv_music, tv_setting;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        RelativeLayout adContainerBanner = findViewById(R.id.adContainerBanner);
        ShimmerFrameLayout shimmerContainerBanner = findViewById(R.id.shimmer_container_banner);
        AdManager.showBanner(adContainerBanner, shimmerContainerBanner, MainActivity.this);

        switchToFragment1(new VideoManagerFragment());
        findViewById(R.id.rl_title).setVisibility(8);
        this.tv_music = (TextView) findViewById(R.id.tv_music);
        this.tv_video = (TextView) findViewById(R.id.tv_video);
        this.tv_setting = (TextView) findViewById(R.id.tv_setting);
        this.iv_icon = (ImageView) findViewById(R.id.iv_icon);

        Glide.with(this)
                .asGif()
                .load(R.drawable.icon)
                .into(iv_icon);

        setTextColor(this.tv_video, this.tv_music, this.tv_setting);

        findViewById(R.id.ll_video).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView) findViewById(R.id.title)).setText(R.string.video);
                findViewById(R.id.rl_title).setVisibility(8);
                view.post(() -> switchToFragment1(new VideoManagerFragment()));

//                switchToFragment1(new VideoManagerFragment());
                ((ImageView) findViewById(R.id.video)).setImageResource(R.drawable.b_videos);
                ((ImageView) findViewById(R.id.music)).setImageResource(R.drawable.u_music);
                ((ImageView) findViewById(R.id.setting)).setImageResource(R.drawable.u_setting);
                MainActivity.this.setTextColor(tv_video, tv_music, tv_setting);
            }
        });
        findViewById(R.id.ll_music).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView) findViewById(R.id.title)).setText(R.string.music_player);
                findViewById(R.id.rl_title).setVisibility(0);
                view.post(() -> switchToFragment1(new MusicManagerFragment()));

//                switchToFragment1(new MusicManagerFragment());
                MainActivity.this.setTextColor(tv_music, tv_video, tv_setting);
                ((ImageView) findViewById(R.id.video)).setImageResource(R.drawable.u_videos);
                ((ImageView) findViewById(R.id.music)).setImageResource(R.drawable.b_music);
                ((ImageView) findViewById(R.id.setting)).setImageResource(R.drawable.u_setting);
            }
        });
        findViewById(R.id.ll_setting).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.rl_title).setVisibility(0);
                ((TextView) findViewById(R.id.title)).setText(R.string.setting);
                view.post(() -> switchToFragment1(new SettingFragment()));
//                switchToFragment1(new SettingFragment());
                ((ImageView) findViewById(R.id.video)).setImageResource(R.drawable.u_videos);
                ((ImageView) findViewById(R.id.music)).setImageResource(R.drawable.u_music);
                ((ImageView) findViewById(R.id.setting)).setImageResource(R.drawable.b_setting);
                MainActivity.this.setTextColor(tv_setting, tv_video, tv_music);
            }
        });

        this.ivThumbnailMusic = (ImageView) findViewById(R.id.iv_thumbnail);
        this.tvSong = (TextView) findViewById(R.id.tv_song);
        this.tvArtist = (TextView) findViewById(R.id.tv_artist);
        this.ivPlay = (ImageView) findViewById(R.id.iv_music_play);
        this.layoutCurrentMusic = (RelativeLayout) findViewById(R.id.layout_song_playing);
        this.timeControl = (ProgressBar) findViewById(R.id.progress_control);


        ivPlay.setOnClickListener(view -> {
            if (musicService != null) {
                if (isPlaying) {
                    stopMusicPlayback();
                } else {
                    startMusicPlayback();
                }
            }
        });

        findViewById(R.id.iv_music_play).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MusicService musicService2 = musicService;
                if (musicService2 != null) {
                    musicService2.playSong();
                }
            }
        });
        findViewById(R.id.iv_music_close).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MusicService musicService2 = musicService;
                if (musicService2 != null) {
                    musicService2.stopService();
                }
            }
        });
        this.layoutCurrentMusic.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!musicIdList.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
                    intent.putExtra(AppConstant.IntentExtra.EXTRA_FROM_NOTIFICATION, true);
                    intent.putExtra(AppConstant.IntentExtra.EXTRA_MUSIC_NUMBER, position);
                    intent.putExtra(AppConstant.IntentExtra.EXTRA_MUSIC_ARRAY, (ArrayList) musicIdList);
                    intent.putExtra(AppConstant.IntentExtra.EXTRA_MUSIC_SONG, currentMusic);
                    startActivity(intent);
                }
            }
        });

        showRatingDialog();
        checkForAppUpdate();
    }


    private void setTextColor(TextView textView, TextView textView2, TextView textView3) {
        textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.app_color));
        textView2.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.gray_light));
        textView3.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.gray_light));

    }

    public void switchToFragment1(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commitAllowingStateLoss(); // ✅ safer in onClick/onResume/onStart cases

//        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }


    public void onResume() {
        super.onResume();
        this.isRunning = true;
        setCurrentMusicContent();
        this.isBindService = bindService(new Intent(this, MusicService.class), this, 1);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, new IntentFilter("RECEIVER_CURRENT_MUSIC"));
//        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, new IntentFilter("MediaPlayer"));


        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo()
                    .addOnSuccessListener(appUpdateInfo -> {
                        if (appUpdateInfo.installStatus() == com.google.android.play.core.install.model.InstallStatus.DOWNLOADED) {
                            popupSnackbarForCompleteUpdate();
                        }
                    });
        }


    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(findViewById(android.R.id.content),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> {
            appUpdateManager.completeUpdate();
        });
        snackbar.show();
    }


    public void onPause() {
        super.onPause();
        this.isRunning = false;
        if (this.isBindService) {
            unbindService(this);
            this.isBindService = false;
        }
        Handler handler = this.handler;
        if (handler != null) {
            handler.removeCallbacks(this.runnable);
        }
        if (isPlaying) {
            stopMusicPlayback();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        if (infodialog != null && infodialog.isShowing()) {
            infodialog.dismiss(); // Dismiss the dialog when the activity is destroyed
        }
    }

    public void setCurrentMusicContent() {
        MusicInfo musicInfo = this.currentMusic;
        if (musicInfo != null) {
            ((RequestBuilder) ((RequestBuilder) Glide.with((FragmentActivity) this).load(MusicPlayerUtils.getThumbnailOfSong(this, musicInfo.getPath(), 40)).centerCrop()).error(R.drawable.ic_music_icon)).into(this.ivThumbnailMusic);
            String artist = this.currentMusic.getArtist();
            if (TextUtils.isEmpty(artist)) {
                this.tvArtist.setVisibility(8);
            } else {
                this.tvArtist.setText(artist);
            }
            this.tvSong.setText(this.currentMusic.getDisplayName());
            this.ivPlay.setImageResource(this.isPlaying ? R.drawable.ic_noti_pause : R.drawable.ic_noti_play);
            this.layoutCurrentMusic.setVisibility(0);
            return;
        }
        this.layoutCurrentMusic.setVisibility(8);
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService service = ((MusicService.MyBinder) iBinder).getService();
        this.musicService = service;
        if (service != null) {
            this.currentMusic = service.getCurrentSong();
            this.position = this.musicService.getIndex();
            this.isPlaying = this.musicService.isSongPlaying();
            this.musicIdList.clear();
            this.musicIdList.addAll(this.musicService.getMusicIdList());
            setCurrentMusicContent();
        }
        runOnUiThread(this.runnable);
    }

    public void onServiceDisconnected(ComponentName componentName) {
        this.musicService = null;
    }

    private void stopVideoPlayback() {
        isVideoPlaying = false;

        // Logic to stop video playback
        Log.d("MainActivity", "Video playback stopped");
    }

    private void startMusicPlayback() {
        if (isVideoPlaying) {
            stopVideoPlayback();
        }
        isPlaying = true;
        ivPlay.setImageResource(R.drawable.ic_noti_pause);
        musicService.playSong();

        Log.d("MainActivity", "Music playback started");
    }

    private void stopMusicPlayback() {
        isPlaying = false;
        ivPlay.setImageResource(R.drawable.ic_noti_play);
        musicService.stopService(); // Assuming there's a `pauseSong` method

        Log.d("MainActivity", "Music playback stopped");
    }

    @Override
    public void onBackPressed() {
        ExitDialog();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1 && iArr.length > 0) {
            i = iArr[0];
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 2) {
            String str = "android.permission.READ_EXTERNAL_STORAGE";
            String str2 = "android.permission.WRITE_EXTERNAL_STORAGE";
            if (ContextCompat.checkSelfPermission(this, str) != 0 || ContextCompat.checkSelfPermission(this, str2) != 0) {
                ActivityCompat.requestPermissions(this, new String[]{str2, str}, 1);
            }
        }
        if (i == UPDATE_REQUEST_CODE) {
            if (i2 != RESULT_OK) {
                Log.d("UpdateFlow", "Update cancelled by user.");
            }
        }
    }

    private void checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            Log.d("UpdateCheck", "Update availability: " + appUpdateInfo.updateAvailability());
            Log.d("UpdateCheck", "Update type allowed (FLEXIBLE): " + appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE));
            Log.d("UpdateCheck", "Version code available: " + appUpdateInfo.availableVersionCode());

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this,
                            UPDATE_REQUEST_CODE);
                    Log.d("UpdateCheck", "Started update flow.");
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("UpdateCheck", "No update available or not allowed.");
            }
        });

        appUpdateInfoTask.addOnFailureListener(e -> {
            Log.e("UpdateCheck", "Update info fetch failed", e);
        });
    }


//    public void checkForAppUpdate() {
//        int currentVersion = getCurrentVersionCode();
//
//        // Replace this value with the version code from your server or API

    /// /        int latestVersionFromServer = BuildConfig.VERSION_CODE; // example: version 1.5
//        int latestVersionFromServer = BuildConfig.VERSION_CODE; // example: version 1.5
//
//        if (latestVersionFromServer > currentVersion) {
//            // Show the update dialog
//            showUpdateDialog();
//        }
//    }

//    public void showUpdateDialog() {
//
//        final Dialog dialog = new Dialog(this);
//        dialog.setCancelable(false);
//        View view = getLayoutInflater().inflate(R.layout.dialog_update, null);
//        dialog.setContentView(view);
//        TextView update = view.findViewById(R.id.tvUpdate);
//        TextView txt_title = view.findViewById(R.id.tvTitle);
//        TextView txt_decription = view.findViewById(R.id.tvMsg);
//        TextView txt_label = view.findViewById(R.id.tvLabel);
//
//        update.setText("Update Now");
//        txt_title.setText(getResources().getString(R.string.update_title));
//        txt_decription.setText(getResources().getString(R.string.update_msg));
//        txt_label.setText(getResources().getString(R.string.update_label));
//
//        update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
//                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
//                    startActivity(marketIntent);
//                } catch (ActivityNotFoundException ignored1) {
//                }
//            }
//        });
//        Window window = dialog.getWindow();
//        //   window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            dialog.create();
//        }
//        dialog.show();
//    }
//
//    public int getCurrentVersionCode() {
//        PackageManager manager = getPackageManager();
//        PackageInfo info = null;
//        try {
//            info = manager.getPackageInfo(getPackageName(), 0);
//            return info.versionCode;
//
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }


//public void showUpdateDialog() {
//
//    final Dialog dialog = new Dialog(this);
//    dialog.setCancelable(false);
//    View view = getLayoutInflater().inflate(R.layout.dialog_update, null);
//    dialog.setContentView(view);
//    TextView update = view.findViewById(R.id.tvUpdate);
//    TextView txt_title = view.findViewById(R.id.tvTitle);
//    TextView txt_decription = view.findViewById(R.id.tvMsg);
//    TextView txt_label = view.findViewById(R.id.tvLabel);
//
//    update.setText("Update Now");
//    txt_title.setText(getResources().getString(R.string.update_title));
//    txt_decription.setText(getResources().getString(R.string.update_msg));
//    txt_label.setText(getResources().getString(R.string.update_label));
//
//    update.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            try {
//                Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
//                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
//                startActivity(marketIntent);
//            } catch (ActivityNotFoundException ignored1) {
//            }
//        }
//    });
//    Window window = dialog.getWindow();
//    //   window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        dialog.create();
//    }
//    dialog.show();
//}


//    private void checkForUpdate() {
//        VersionChecker versionChecker = new VersionChecker();
//        try {
//            String currentVersion = BuildConfig.VERSION_NAME;
//            String latestVersion = versionChecker.execute().get();
//
//            Log.d("UpdateCheck", "Current: " + currentVersion + ", Latest: " + latestVersion);
//
//            if (latestVersion != null && !currentVersion.trim().equalsIgnoreCase(latestVersion.trim())) {
//                showUpdateDialog();
//            }
//
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
    private void ExitDialog() {
        if (infodialog != null && infodialog.isShowing()) {
            return; // If the dialog is already showing, do nothing
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_exit, null);
        builder.setView(dialogView);
        infodialog = builder.create();
        infodialog.setCancelable(false);
        AppCompatButton tv_ok = dialogView.findViewById(R.id.tv_dialog_ok);
        TextView tv_cancel = dialogView.findViewById(R.id.tv_dialog_cancel);

        RelativeLayout adContainerBanner = dialogView.findViewById(R.id.adContainerBannerExit);
        ShimmerFrameLayout shimmerFrameLayout = dialogView.findViewById(R.id.shimmer_container_banner_exit);
        AdManager.showNativeBig(adContainerBanner, shimmerFrameLayout, MainActivity.this);

        tv_ok.setOnClickListener(view -> {
            finishAffinity();
            infodialog.dismiss();
        });

        tv_cancel.setOnClickListener(view -> infodialog.dismiss());
        infodialog.show();
    }

    private void showRatingDialog() {
        try {
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int counter = app_preferences.getInt("counter", 0);
            int RunEvery = 8;
            if (counter != 0 && counter % RunEvery == 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialog);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_rating, null);
                builder.setView(dialogView);

                AppCompatButton btnSubmit = dialogView.findViewById(R.id.tv_submit);
                TextView btnCancel = dialogView.findViewById(R.id.tv_maybe_later);

                AlertDialog exitDialog = builder.create();
                exitDialog.setCancelable(false);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitDialog.cancel();
                    }
                });
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent openPlayStore = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(openPlayStore);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainActivity.this, " unable to find market app", Toast.LENGTH_LONG).show();
                        }
                        exitDialog.dismiss();
                    }
                });

                exitDialog.show();
            }
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putInt("counter", ++counter);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}

