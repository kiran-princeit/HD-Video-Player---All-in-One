package com.hdvideoplayer.smartplayer.player.adsprosimple.all_ads;

import static  com.hdvideoplayer.smartplayer.player.adsprosimple.GlobalVar.appData;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hdvideoplayer.smartplayer.player.adsprosimple.RemoteAppDataModel;
import com.hdvideoplayer.smartplayer.player.MyApplication;

public class allBanner {
    public static void showBanner(RelativeLayout relativeLayout, ShimmerFrameLayout shimmerFrameLayout, Activity activity) {


        if (appData == null) {
            appData = new RemoteAppDataModel();
            Log.e("showBanner", "appData is null");
            return;
        }
        if (!MyApplication.isNetworkConnected(activity)) {
            relativeLayout.setVisibility(View.GONE);
            return;
        }
        if (appData.getshowBanner() == null || appData.getadstype() == null) {
            Log.e("showBanner", "showBanner or adstype is null");
            return;
        }

        if (appData.getshowBanner() == 1) {
            switch (appData.getadstype()) {
                case "facebook":
                    fbBannerAds(relativeLayout, shimmerFrameLayout, activity);
                    break;
                case "admob":
                    loadAdMobBanner(relativeLayout, shimmerFrameLayout, activity);
                    break;
                case "adx":
                    loadAdxBanner(relativeLayout, shimmerFrameLayout, activity);
                    break;
                default:
                    relativeLayout.setVisibility(View.GONE);
                    break;
            }
        } else {
            relativeLayout.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
        }
    }

    public static void fbBannerAds(RelativeLayout BannerContainer, ShimmerFrameLayout shimmerFrameLayout, Activity activity) {
        try {
            BannerContainer.setVisibility(View.VISIBLE);

            final com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, appData.getfbBannerid(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);

            com.facebook.ads.AdListener adlistner = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    BannerContainer.removeAllViews();
                    BannerContainer.setVisibility(View.INVISIBLE);

//                    BannerContainer.setVisibility(View.GONE);
                    if (shimmerFrameLayout.isShimmerStarted()) {
                        shimmerFrameLayout.stopShimmer();
                    }
                    shimmerFrameLayout.setVisibility(View.GONE);

                }

                @Override
                public void onAdLoaded(Ad ad) {

                    try {

                        BannerContainer.setVisibility(View.VISIBLE);
                        if (shimmerFrameLayout.isShimmerStarted()) {
                            shimmerFrameLayout.stopShimmer();
                        }
                        shimmerFrameLayout.setVisibility(View.GONE);
                        BannerContainer.setPadding(3, 3, 3, 3);
//                            BannerContainer.setBackgroundColor(Color.parseColor("#FFC107"));
                        BannerContainer.removeAllViews();
                        BannerContainer.addView(adView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };

            // Request an ad
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adlistner).build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadAdMobBanner(RelativeLayout adContainerView, ShimmerFrameLayout shimmerFrameLayout, Activity activity) {
        if (appData.getshowBanner() == 1) {
            String adUnitId = appData.getamBannerid();

            if (TextUtils.isEmpty(adUnitId)) {
                return;
            }

            //BannerAd
            AdView admobManagerAdView = new AdView(activity);
            admobManagerAdView.setAdUnitId(adUnitId);
            adContainerView.addView(admobManagerAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            AdSize adSize = getAdSize(adContainerView, activity);
            admobManagerAdView.setAdSize(adSize);
            admobManagerAdView.loadAd(adRequest);

            admobManagerAdView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    adContainerView.setVisibility(View.VISIBLE);
                    if (shimmerFrameLayout.isShimmerStarted()) {
                        shimmerFrameLayout.stopShimmer();
                    }
                    shimmerFrameLayout.setVisibility(View.GONE);
                    Log.e("BannerAd", "BannerAd ==> onAdLoaded");
                    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(activity);
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, "banner");
                    bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId);
                    bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, "admob");
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);

                    super.onAdLoaded();

                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    adContainerView.setVisibility(View.GONE);
                    Log.e("BannerAd", "BannerAd ==> onAdFailedToLoad " + loadAdError);
                    if (shimmerFrameLayout.isShimmerStarted()) {
                        shimmerFrameLayout.stopShimmer();
                    }
                    shimmerFrameLayout.setVisibility(View.GONE);
                    super.onAdFailedToLoad(loadAdError);
                }
            });
        } else {
            shimmerFrameLayout.setVisibility(View.GONE);
        }
    }


    public static void loadAdxBanner(RelativeLayout adContainerView, ShimmerFrameLayout shimmerFrameLayout, Activity activity) {
        if (appData.getshowBanner() == 1) {
            String adUnitId = appData.getAdxbannerid();

            Log.e("loadAdxBanner", "loadAdxBanner: "+adUnitId );

            if (TextUtils.isEmpty(appData.getAdxbannerid())) {
                shimmerFrameLayout.setVisibility(View.GONE);
                return;
            }
            AdView admobManagerAdView = new AdView(activity);
            admobManagerAdView.setAdUnitId(appData.getAdxbannerid());
            adContainerView.addView(admobManagerAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            AdSize adSize = getAdSize(adContainerView, activity);
            admobManagerAdView.setAdSize(adSize);
            admobManagerAdView.loadAd(adRequest);
            admobManagerAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    adContainerView.setVisibility(View.VISIBLE);
                    if (shimmerFrameLayout.isShimmerStarted()) {
                        shimmerFrameLayout.stopShimmer();
                    }
                    shimmerFrameLayout.setVisibility(View.GONE);
                    Log.e("BannerAdx", "BannerAdx ==> onAdLoaded");
                    super.onAdLoaded();

                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    adContainerView.setVisibility(View.GONE);
                    Log.e("BannerAdx", "BannerAdx ==> onAdFailedToLoad " + loadAdError);
                    if (shimmerFrameLayout.isShimmerStarted()) {
                        shimmerFrameLayout.stopShimmer();
                    }
                    shimmerFrameLayout.setVisibility(View.GONE);
                    super.onAdFailedToLoad(loadAdError);
                }
            });
        } else {
            shimmerFrameLayout.setVisibility(View.GONE);
        }
    }


    private static AdSize getAdSize(ViewGroup adContainerView, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Rect bounds = windowMetrics.getBounds();

            float adWidthPixels = adContainerView.getWidth();

            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0f) {
                adWidthPixels = bounds.width();
            }

            float density = activity.getResources().getDisplayMetrics().density;
            int adWidth = (int) (adWidthPixels / density);

            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
        } else {
            DisplayMetrics outMetrics = activity.getResources().getDisplayMetrics();
            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;
            int adWidth = (int) (widthPixels / density);
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
        }
    }
}