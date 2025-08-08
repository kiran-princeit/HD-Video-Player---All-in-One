package com.hdvideoplayer.smartplayer.player.adsprosimple;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hdvideoplayer.smartplayer.player.adsprosimple.all_ads.allAppopen;
import com.hdvideoplayer.smartplayer.player.adsprosimple.all_ads.allBanner;
import com.hdvideoplayer.smartplayer.player.adsprosimple.all_ads.allNative;
import com.hdvideoplayer.smartplayer.player.adsprosimple.all_ads.allNativesmall;
import com.hdvideoplayer.smartplayer.player.adsprosimple.all_ads.allInterstitial;

@Keep
public interface AdManager {

    interface firebaseonloadcomplete {
        void onloadcomplete();
    }

    static void init(Context context, firebaseonloadcomplete firebasecompte) {
        Firebase_Helper.loadFirebase(context, firebasecompte);
    }

    static void showAppopen(Activity activity, OnActivityResultLauncher1.OnActivityResultLauncher2 resultLauncher) {
        allAppopen.showAppopen(activity, resultLauncher);
    }


    static void showBanner(final RelativeLayout BannerContainer, ShimmerFrameLayout shimmerFrameLayout, Activity activity) {
        allBanner.showBanner(BannerContainer, shimmerFrameLayout, activity);
    }
    static void showNativeBig(final RelativeLayout nativeContainer, ShimmerFrameLayout shimmerFrameLayout, Activity activity) {
        allNative.showNativeBig(nativeContainer, shimmerFrameLayout,activity);
    }

    static void showNativeSmall(final RelativeLayout nativeContainer, ShimmerFrameLayout shimmerFrameLayout, Activity activity) {
        allNativesmall.showNativeSmall(nativeContainer,shimmerFrameLayout, activity);
    }

    static void showInterstitial(Activity activity, OnActivityResultLauncher1.OnActivityResultLauncher2 resultLauncher) {
        allInterstitial.showInterstitial(activity, resultLauncher);
    }
}