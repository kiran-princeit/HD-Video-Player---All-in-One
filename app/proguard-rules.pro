# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}


-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.facebook.** { *; }

# Keep Parcelable classes
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.facebook.ads.AudienceNetworkSDK { *; }
-keep class com.facebook.ads.Ad { *; }
-keep class com.facebook.ads.AdError { *; }
-keep class com.facebook.ads.AdListener { *; }
-keep class com.facebook.ads.AdSettings { *; }
-keep class com.facebook.ads.AdView { *; }
-keep class com.facebook.ads.InterstitialAd { *; }
-keep class com.facebook.ads.NativeAd { *; }
-keep class com.facebook.ads.NativeAdView { *; }
-keep class com.facebook.ads.RewardedVideoAd { *; }
-keep class com.facebook.ads.VideoAdView { *; }
-keep class com.facebook.ads.placement.Placement { *; }

-keepclassmembers class com.facebook.ads.AudienceNetworkSDK {
    public void <init>();
    public void setMediationListener(com.facebook.ads.mediation.MediationListener);
    public void initialize(android.content.Context, java.lang.String);
}

-keepclassmembers class com.facebook.ads.Ad {
    public void setAdListener(com.facebook.ads.AdListener);
    public void loadAd();
    public void show();
    public void destroy();
}

-keepclassmembers class com.facebook.ads.AdView {
    public void setAdListener(com.facebook.ads.AdListener);
    public void loadAd();
}

-keepclassmembers class com.facebook.ads.InterstitialAd {
    public void setAdListener(com.facebook.ads.AdListener);
    public void loadAd();
    public void show();
}

-keepclassmembers class com.facebook.ads.NativeAd {
    public void setAdListener(com.facebook.ads.AdListener);
    public void registerViewForInteraction(android.view.View, java.util.Map);
    public void loadAd();
}

-keepclassmembers class com.facebook.ads.RewardedVideoAd {
    public void setAdListener(com.facebook.ads.AdListener);
    public void loadAd();
    public void show();
}

-dontwarn com.facebook.ads.**