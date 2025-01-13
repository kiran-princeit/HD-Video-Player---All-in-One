package hd.video.player.videoplayer.mplayer.masterplayer.activities;

import static hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.GlobalVar.appData;

import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import hd.video.player.videoplayer.mplayer.masterplayer.MyApplication;
import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.Const;
import hd.video.player.videoplayer.mplayer.masterplayer.Pref;
import hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.AdManager;
import hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.RemoteAppDataModel;
import hd.video.player.videoplayer.mplayer.masterplayer.util.LanguagePreference;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;

public class SplashActivity extends BaseActivity {
    private static final String LOG_TAG = "SplashActivity";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private static final long COUNTER_TIME_MILLISECONDS = 5000;
    private long secondsRemaining;
    private Dialog dialog;
    private boolean isNetworkConnected = false;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean isFirebaseDataLoaded = false;  // Flag to track if Firebase data is loaded

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        Utility.initFolderPath(this);

        appData = new RemoteAppDataModel();
        ImageView splashLoading = findViewById(R.id.splashLoading);
        ImageView splashImageView = findViewById(R.id.splashImageView);

        Glide.with(this)
                .asGif()
                .load(R.drawable.splash_gif)
                .into(splashLoading);

        Glide.with(this)
                .asGif()
                .load(R.drawable.splashlogo_gif)
                .into(splashImageView);


        com.google.android.gms.ads.MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(
                            @NonNull InitializationStatus initializationStatus) {
                    }
                });
        AdManager.init(this, new AdManager.firebaseonloadcomplete() {
            @Override
            public void onloadcomplete() {
            }
        });
        createTimer();

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        checkNetworkStatus();
    }

    private void moveNext() {
        new Handler().postDelayed(() -> {
            Intent intent;
            Pref instance = Pref.getInstance();
            String str = Const.NEXT;
            if (instance.getString(str).equals("language")) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, LanguageActivity.class);
            }
            intent.setFlags(603979776);
            startActivity(intent);
            finish();
        }, 2000);


    }

    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isNetworkConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isNetworkConnected) {
            loadFirebaseData();
        } else {
            showNoInternetDialog();
        }
    }

    private void loadFirebaseData() {
        AdManager.init(this, new AdManager.firebaseonloadcomplete() {
            @Override
            public void onloadcomplete() {
                if (appData != null) {
                    Log.e("data", "SplashActivity firebase data complete!");
                    isFirebaseDataLoaded = true;
                    if (appData.getshowOpenad() == 1) {
                        Log.e("data", "App should show open ad.");
                        initializeAds();
//                    } else {
                        Log.e("data", "App should skip the ad.");
//                        moveNext();
                    }
                } else {
                    Log.e("data", "appData is null, retrying...");
                    retryLoadData();
                }
            }
        });
    }

    private void retryLoadData() {
        new Handler().postDelayed(() -> {
            Log.e("data", "Retrying Firebase data load...");
            loadFirebaseData();
        }, 2000);
    }

    public void showNoInternetDialog() {
        dialog = new Dialog(SplashActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_nointernet);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        AppCompatButton btnPositive = dialog.findViewById(R.id.btnTyrAgain);

        btnPositive.setOnClickListener(v -> {
            if (isNetworkConnected) {
                dialog.dismiss();
                loadFirebaseData();
            } else {
                Toast.makeText(SplashActivity.this, "Still no internet", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void createTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(COUNTER_TIME_MILLISECONDS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1;
            }

            @Override
            public void onFinish() {
                secondsRemaining = 0;
                Application application = getApplication();
                ((MyApplication) application)
                        .showAdIfAvailable(
                                SplashActivity.this,
                                new MyApplication.OnShowAdCompleteListener() {
                                    @Override
                                    public void onShowAdComplete() {
                                        moveNext();
                                    }
                                });
            }
        };
        countDownTimer.start();
    }

    private void initializeAds() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        com.google.android.gms.ads.MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder()
                        .setTestDeviceIds(Arrays.asList(MyApplication.TEST_DEVICE_HASHED_ID))
                        .build());

        new Thread(() -> {
            com.google.android.gms.ads.MobileAds.initialize(this, initializationStatus -> {
            });
            runOnUiThread(() -> {
                Application application = getApplication();
                ((MyApplication) application).loadAd(this);  // Load and show the ad
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

//            if (isConnected) {
//                Log.d("NetworkChangeReceiver", "Network connected");
//                if (!isNetworkConnected) {
//                    isNetworkConnected = true;
////                    loadFirebaseData();
//                    moveNext();
//                }
//            } else {
//                Log.d("NetworkChangeReceiver", "No internet connection");
//                if (isNetworkConnected) {
//                    isNetworkConnected = false;
//                    showNoInternetDialog();
//                }
//            }
        }
    }


}
