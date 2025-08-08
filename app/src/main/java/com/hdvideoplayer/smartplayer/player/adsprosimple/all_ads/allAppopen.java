package com.hdvideoplayer.smartplayer.player.adsprosimple.all_ads;

import static  com.hdvideoplayer.smartplayer.player.adsprosimple.GlobalVar.appData;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.hdvideoplayer.smartplayer.player.MyApplication;
import com.hdvideoplayer.smartplayer.player.adsprosimple.OnActivityResultLauncher1;

public class allAppopen {

    public static void showAppopen(Activity activity, OnActivityResultLauncher1.OnActivityResultLauncher2 resultLauncher) {

        if (!MyApplication.isNetworkConnected(activity)) {
            resultLauncher.onLauncher();
            return;
        }

        if (appData.getshowOpenad() != 1) {
            resultLauncher.onLauncher();
            return;
        }

        switch (appData.getadstype()) {
            case "admob":
                amappopen(activity, resultLauncher);
                break;

//            case "custom":
//                customappopen(activity, resultLauncher);
//                break;

            case "facebook":
                amappopen(activity, resultLauncher);
            default:
                resultLauncher.onLauncher();
                break;
        }

    }

    public static void amappopen(Activity act, OnActivityResultLauncher1.OnActivityResultLauncher2 resultLauncher) {
        String adUnitId = appData.getamOpenadid();
        if (adUnitId.isEmpty()) {
            return;
        }

//        if (BuildConfig.DEBUG) {
        // Test Ad ID for debug builds
//            adUnitId = "ca-app-pub-3940256099942544/3419835294";
//        } else {
        // Ad ID from Firebase Remote Config for release builds
        adUnitId = appData.getamOpenadid();
//        }

        AppOpenAd.load(act, adUnitId, new AdRequest.Builder().build(), AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        resultLauncher.onLauncher();

//                            Log.e("XXX", "onAdDismissedFullScreenContent");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                        resultLauncher.onLauncher();
//                            Log.e("XXX", "onAdFailedToShowFullScreenContent");

                    }
                });
                ad.show(act);

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                resultLauncher.onLauncher();
//                    Log.e("XXX", "onAdFailedToLoad");

            }
        });
    }

    static Dialog appopendialog;
    static LinearLayout ll_continue;
    static RelativeLayout rl_qureka;


    public static void openUrlInChromeCustomTab(Context context, String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabColorSchemeParams colorSchemeParams = new CustomTabColorSchemeParams.Builder().build();
            builder.setDefaultColorSchemeParams(colorSchemeParams);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));

//            qurekalinkarraycount++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}