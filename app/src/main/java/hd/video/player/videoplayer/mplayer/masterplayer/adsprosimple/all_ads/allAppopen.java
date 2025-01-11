package hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.all_ads;

import static  hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.GlobalVar.appData;

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
import hd.video.player.videoplayer.mplayer.masterplayer.MyApplication;
import  hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.OnActivityResultLauncher1;

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

//    public static void customappopen(Activity act, OnActivityResultLauncher1.OnActivityResultLauncher2 resultLauncher) {
//        try {
//            if (qurekalinkarraycount > strQurekaLinkArray.length - 1)
//                qurekalinkarraycount = 0;
//
//            Dialog dialog = new Dialog(act);
//            appopendialog = dialog;
//            dialog.requestWindowFeature(1);
//            appopendialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//            appopendialog.setContentView(R.layout.custom_appopen);
//            appopendialog.getWindow().setLayout(-1, -2);
//            appopendialog.setCancelable(false);
//            appopendialog.show();
//            GifImageView gifImageView = appopendialog.findViewById(R.id.gif_app_open);
//            TextView textView = appopendialog.findViewById(R.id.tv_header_text);
//            ImageView imageView = appopendialog.findViewById(R.id.iv_qureka_img);
//            int nextInt = new Random().nextInt(3);
//            if (nextInt == 1) {
//                textView.setText("Guess And Win Coins");
//                imageView.setBackgroundResource(R.drawable.qureka_open2);
//                gifImageView.setBackgroundResource(R.drawable.qureka_round2);
//            } else if (nextInt == 2) {
//                textView.setText("Play Cricket Quiz");
//                imageView.setBackgroundResource(R.drawable.qureka_open3);
//                gifImageView.setBackgroundResource(R.drawable.qureka_round3);
//            } else {
//                textView.setText("Mega Prediction Contest");
//                imageView.setBackgroundResource(R.drawable.qureka_open5);
//                gifImageView.setBackgroundResource(R.drawable.qureka_round_5);
//            }
//            appopendialog.findViewById(R.id.btn_play_now1).setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    resultLauncher.onLauncher();
//                    appopendialog.dismiss();
//
//                    openUrlInChromeCustomTab(act, strQurekaLinkArray[qurekalinkarraycount]);
//                }
//            });
//            appopendialog.findViewById(R.id.btn_play_now).setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    resultLauncher.onLauncher();
//                    appopendialog.dismiss();
//
//                    openUrlInChromeCustomTab(act, strQurekaLinkArray[qurekalinkarraycount]);
//                }
//            });
//            ll_continue = appopendialog.findViewById(R.id.ll_continue);
//            rl_qureka = appopendialog.findViewById(R.id.rl_qureka);
//            ll_continue.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    appopendialog.dismiss();
//                    resultLauncher.onLauncher();
//                    openUrlInChromeCustomTab(act, strQurekaLinkArray[qurekalinkarraycount]);
//
//                }
//            });
//            rl_qureka.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    resultLauncher.onLauncher();
//
//                    openUrlInChromeCustomTab(act, strQurekaLinkArray[qurekalinkarraycount]);
//                }
//            });
//            appopendialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                    if (i != 4) {
//                        return true;
//                    }
//                    appopendialog.dismiss();
//                    resultLauncher.onLauncher();
//
//                    openUrlInChromeCustomTab(act, strQurekaLinkArray[qurekalinkarraycount]);
//
//                    return true;
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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