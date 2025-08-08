-flattenpackagehierarchy

-keep class com.facebook.** { *; }
-dontwarn com.facebook.**

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class org.jsoup.** { *; }
-keepclassmembers class org.jsoup.** { *; }
-keepattributes *Annotation*, InnerClasses





