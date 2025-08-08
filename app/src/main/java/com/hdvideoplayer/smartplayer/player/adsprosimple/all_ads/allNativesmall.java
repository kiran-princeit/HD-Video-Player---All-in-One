package com.hdvideoplayer.smartplayer.player.adsprosimple.all_ads;

import static  com.hdvideoplayer.smartplayer.player.adsprosimple.GlobalVar.appData;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hdvideoplayer.smartplayer.player.MyApplication;
import  com.hdvideoplayer.smartplayer.player.R;

import java.util.Objects;

public class allNativesmall {

    public static void showNativeSmall(final ViewGroup nativeContainer, ShimmerFrameLayout shimmerFrameLayout, Activity activity) {

        if (!MyApplication.isNetworkConnected(activity)) {
            nativeContainer.setVisibility(View.GONE);
            return;
        }

        if (appData.getshowNative() == 1) {
            switch (appData.getadstype()) {
                case "facebook":
                    fbNativeAdsSmall(activity,shimmerFrameLayout, nativeContainer);
                    break;

                case "admob":
                    amNativeAdsSmall(activity, shimmerFrameLayout, nativeContainer);
                    break;

//                case "custom":
//                    customNativeAds(activity, nativeContainer);
//                    break;

                default:
                    nativeContainer.setVisibility(View.GONE);
                    break;
            }
        } else {
            nativeContainer.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.GONE);
        }
    }

    public static void fbNativeAdsSmall(final Activity context, ShimmerFrameLayout shimmerFrameLayout, final ViewGroup NativeContainer) {
        try {
//                if (fbnativearraycount > strFBNative.length - 1)
//                    fbnativearraycount = 0;
//                final com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(context, strFBNative[fbnativearraycount]);

            final com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(context, appData.getfbNativeid());

            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {

                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    try {

                        NativeContainer.removeAllViews();
                        NativeContainer.setVisibility(View.INVISIBLE);
                        if (shimmerFrameLayout.isShimmerStarted()) {
                            shimmerFrameLayout.stopShimmer();
                        }
                        shimmerFrameLayout.setVisibility(View.GONE);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {
//                        Log.e("xxx", "FB Loaded ! " + fbnativearraycount);
//                        fbnativearraycount++;

                    try {

//                            Log.e("xxx", "Loaded FB");
                        if (shimmerFrameLayout.isShimmerStarted()) {
                            shimmerFrameLayout.stopShimmer();
                        }
                        shimmerFrameLayout.setVisibility(View.GONE);
                        View adView = com.facebook.ads.NativeAdView.render(context, nativeAd, com.facebook.ads.NativeAdView.Type.HEIGHT_300);
                        NativeContainer.removeAllViews();
                        NativeContainer.setPadding(3, 3, 3, 3);
//                            NativeContainer.setBackgroundColor(Color.parseColor("#FFC107"));
                        NativeContainer.addView(adView);


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
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //****************************************************************************************************

    static NativeAd mPack1NativeAdsSmall = null;

    public static void amNativeAdsSmall(Activity activity, ShimmerFrameLayout shimmerFrameLayout, ViewGroup fl_adplaceholder) {
        String adUnitId = appData.getamNativeid();
        if (TextUtils.isEmpty(adUnitId)) {
            return;
        }

        AdLoader.Builder builder = new AdLoader.Builder(activity, adUnitId);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {

                mPack1NativeAdsSmall = nativeAd;
                showNativeOptionAdSmall(fl_adplaceholder, activity);
                if (shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                }
                shimmerFrameLayout.setVisibility(View.GONE);
                // ✅ Firebase Analytics Ad Impression Logging
                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(activity);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, "native");
                bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId);
                bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, "admob");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);
            }
        });


        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                fl_adplaceholder.removeAllViews();
                showNativeSmall(fl_adplaceholder, shimmerFrameLayout, activity);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                }
                shimmerFrameLayout.setVisibility(View.GONE);

            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }


    public static void showNativeOptionAdSmall(ViewGroup fl_adplaceholder, Activity activity) {
        try {
            if (mPack1NativeAdsSmall != null) {

                NativeAdView adView = (NativeAdView) LayoutInflater.from(activity).inflate(R.layout.ad_unifiled_small, null);

                populateUnifiedNativeRegularAdView(mPack1NativeAdsSmall, adView);

                fl_adplaceholder.setVisibility(View.VISIBLE);
                fl_adplaceholder.removeAllViews();
                fl_adplaceholder.addView(adView);
            } else {
                fl_adplaceholder.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fl_adplaceholder.setVisibility(View.GONE);
        }
    }

    public static void populateUnifiedNativeRegularAdView(NativeAd nativeAd, NativeAdView adView) {

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(nativeAd.getHeadline());
        adView.setHeadlineView(headlineView);


        TextView bodyView = adView.findViewById(R.id.ad_body);
        TextView callToActionView = adView.findViewById(R.id.ad_call_to_action);
        ImageView iconView = adView.findViewById(R.id.ad_app_icon);
        TextView priceView = adView.findViewById(R.id.ad_price);
        RatingBar starRatingView = adView.findViewById(R.id.ad_stars);
        TextView storeView = adView.findViewById(R.id.ad_store);
        TextView advertiserView = adView.findViewById(R.id.ad_advertiser);


        if (nativeAd.getBody() == null) {
            bodyView.setVisibility(View.INVISIBLE);
        } else {
            bodyView.setVisibility(View.VISIBLE);
            bodyView.setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            callToActionView.setVisibility(View.INVISIBLE);
        } else {
            callToActionView.setVisibility(View.VISIBLE);
            callToActionView.setText(nativeAd.getCallToAction());
            adView.setCallToActionView(callToActionView);
        }

        if (nativeAd.getIcon() == null) {
            iconView.setVisibility(View.INVISIBLE);
        } else {
            iconView.setVisibility(View.VISIBLE);
            iconView.setImageDrawable(nativeAd.getIcon().getDrawable());
        }

        if (nativeAd.getPrice() == null) {
            priceView.setVisibility(View.INVISIBLE);
        } else {
            priceView.setVisibility(View.VISIBLE);
            priceView.setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            storeView.setVisibility(View.INVISIBLE);
        } else {
            storeView.setVisibility(View.VISIBLE);
            storeView.setText(nativeAd.getStore());
        }

        if (nativeAd.getStore() == null) {
            starRatingView.setVisibility(View.INVISIBLE);
        } else {
            starRatingView.setVisibility(View.VISIBLE);
            starRatingView.setRating(Objects.requireNonNull(nativeAd.getStarRating()).floatValue());
        }


        if (nativeAd.getAdvertiser() == null) {
            advertiserView.setVisibility(View.INVISIBLE);
        } else {
            advertiserView.setVisibility(View.VISIBLE);
            advertiserView.setText(nativeAd.getAdvertiser());
        }

        storeView.setVisibility(View.GONE);
        priceView.setVisibility(View.GONE);

        adView.setNativeAd(nativeAd);

        VideoController vc = Objects.requireNonNull(nativeAd.getMediaContent()).getVideoController();

        if (vc.hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }

    }

    //****************************************************************************************************

//    public static View customNativeAds(final Context context, final ViewGroup adContainerView) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View inflate;
//        inflate = inflater.inflate(R.layout.custom_native, null, false);
//
//        TextView textView = inflate.findViewById(R.id.tv_text_ad_name);
//        TextView textView2 = inflate.findViewById(R.id.tv_text_ad_desc);
//        GifImageView gifImageView = inflate.findViewById(R.id.iv_round_gif);
//        int nextInt = new Random().nextInt(5);
//        if (nextInt == 1) {
//            textView.setText("Predict Match Results");
//            textView2.setText("Win 5,00,000 Coins & More");
//            inflate.findViewById(R.id.rl_qurekha).setBackgroundResource(R.drawable.qureka_native1);
//            gifImageView.setBackgroundResource(R.drawable.qureka_round1);
//        } else if (nextInt == 2) {
//            textView.setText("Bollywood Stars Prediction");
//            textView2.setText("Win 50,000 Coins With Mobile Games");
//            inflate.findViewById(R.id.rl_qurekha).setBackgroundResource(R.drawable.qureka_native2);
//            gifImageView.setBackgroundResource(R.drawable.qureka_round2);
//        } else if (nextInt == 3) {
//            textView.setText("Mega Quiz Games");
//            textView2.setText("Win 50,000 Coins No Install Required");
//            inflate.findViewById(R.id.rl_qurekha).setBackgroundResource(R.drawable.qureka_native3);
//            gifImageView.setBackgroundResource(R.drawable.qureka_round3);
//        } else if (nextInt == 4) {
//            textView.setText("Bank Exams Quiz");
//            textView2.setText("Collect 50,000 Coins Now");
//            inflate.findViewById(R.id.rl_qurekha).setBackgroundResource(R.drawable.qureka_native4);
//            gifImageView.setBackgroundResource(R.drawable.qureka_round_4);
//        } else {
//            textView.setText("Celebrity Prediction");
//            textView2.setText("Win Coin & No Installation Required");
//            inflate.findViewById(R.id.rl_qurekha).setBackgroundResource(R.drawable.qureka_native5);
//            gifImageView.setBackgroundResource(R.drawable.qureka_round_5);
//        }
//        inflate.findViewById(R.id.playNowLL).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (qurekalinkarraycount > strQurekaLinkArray.length - 1)
//                    qurekalinkarraycount = 0;
//                openUrlInChromeCustomTab(context, strQurekaLinkArray[qurekalinkarraycount]);
//
//            }
//        });
//
//        adContainerView.addView(inflate);
//
//        return inflate;
//    }
//
//    public static void openUrlInChromeCustomTab(Context context, String url) {
//        try {
//            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//            CustomTabColorSchemeParams colorSchemeParams = new CustomTabColorSchemeParams.Builder().build();
//            builder.setDefaultColorSchemeParams(colorSchemeParams);
//            CustomTabsIntent customTabsIntent = builder.build();
//            customTabsIntent.launchUrl(context, Uri.parse(url));
//
//            qurekalinkarraycount++;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}