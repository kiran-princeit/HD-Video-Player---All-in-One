package hd.video.player.videoplayer.mplayer.masterplayer.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jsoup.Jsoup;

import hd.video.player.videoplayer.mplayer.masterplayer.BuildConfig;
import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.AdManager;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.SettingFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.music.MusicManagerFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.video.VideoManagerFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.services.MusicService;
import hd.video.player.videoplayer.mplayer.masterplayer.util.PermissionUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.music.MusicPlayerUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant;

public class MainActivity extends BaseActivity implements ServiceConnection {
    private MusicInfo currentMusic;
    Handler handler = new Handler();
    private boolean isBindService = false;
    private boolean isPlaying = false;
    private boolean isRunning = false;

    private ImageView ivPlay;

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
    private TextView tvSong, tv_video, tv_music, tv_setting;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        RelativeLayout adContainerBanner = findViewById(R.id.adContainerBanner);
        ShimmerFrameLayout shimmerContainerBanner = findViewById(R.id.shimmer_container_banner);
        AdManager.showBanner(adContainerBanner, shimmerContainerBanner, MainActivity.this);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!(Settings.System.canWrite(this) && PermissionUtils.checkStoragePermission(this))) {
//                startActivity(new Intent(this, PermissionActivity.class));
//            }
//        }
        switchToFragment1(new VideoManagerFragment());
        findViewById(R.id.rl_title).setVisibility(8);
        this.tv_music = (TextView) findViewById(R.id.tv_music);
        this.tv_video = (TextView) findViewById(R.id.tv_video);
        this.tv_setting = (TextView) findViewById(R.id.tv_setting);

        setTextColor(this.tv_video, this.tv_music, this.tv_setting);

        findViewById(R.id.ll_video).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView) findViewById(R.id.title)).setText(R.string.video);
                findViewById(R.id.rl_title).setVisibility(8);
                switchToFragment1(new VideoManagerFragment());
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
                switchToFragment1(new MusicManagerFragment());
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
                switchToFragment1(new SettingFragment());
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
        checkForUpdate();
    }


    private void setTextColor(TextView textView, TextView textView2, TextView textView3) {
        textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.app_color));
        textView2.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.gray_light));
        textView3.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.gray_light));

    }

    public void switchToFragment1(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        ExitDialog();

    }

    private AlertDialog infodialog;

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
    }

    public void onResume() {
        super.onResume();
        this.isRunning = true;
        setCurrentMusicContent();
        this.isBindService = bindService(new Intent(this, MusicService.class), this, 1);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, new IntentFilter("RECEIVER_CURRENT_MUSIC"));
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

    private void checkForUpdate() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0);
            int curVersionCode = packageInfo.versionCode;
            if (curVersionCode > 1) {  // instead of one use value get from server for the new update.
                CheckUPdate();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void CheckUPdate() {
        VersionChecker versionChecker = new VersionChecker();
        try {
            String appVersionName = BuildConfig.VERSION_NAME;
            String mLatestVersionName = versionChecker.execute().get();
            if (!appVersionName.equals(mLatestVersionName)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialog);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_update, null);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);

                AppCompatButton btnUpdateNow = dialogView.findViewById(R.id.btnUpdateNow);
                btnUpdateNow.setOnClickListener(view -> {
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                });
                alertDialog.show();
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VersionChecker extends AsyncTask<String, String, String> {
        private String newVersion;

        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName())
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;
        }
    }
}
