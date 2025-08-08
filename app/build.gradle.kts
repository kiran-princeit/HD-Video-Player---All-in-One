plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.hdvideoplayer.smartplayer.player"
    compileSdk = 35
    useLibrary("org.apache.http.legacy")

    defaultConfig {
        applicationId = "com.hdvideoplayer.smartplayer.player"
        minSdk = 21
        targetSdk = 35
        versionCode = 8
        versionName = "1.7"
        multiDexEnabled = true
        renderscriptTargetApi = 21
        renderscriptSupportModeEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("D:\\HDVideoPlayerAllinOne\\app\\hdvideoplayerallinone.jks")
            storePassword = "Prince@9313"
            keyAlias = "key0"
            keyPassword = "Prince@9313"
        }
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "false"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "true"
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        pickFirst("lib/x86/libc++_shared.so")
        pickFirst("lib/x86_64/libc++_shared.so")
        pickFirst("lib/armeabi-v7a/libc++_shared.so")
        pickFirst("lib/arm64-v8a/libc++_shared.so")
    }

    ndkVersion = "28.0.12674087 rc2"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.google.firebase:firebase-crashlytics:19.4.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.airbnb.android:lottie:6.6.2")
    implementation("com.karumi:dexter:6.2.3")
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.exoplayer:exoplayer:2.18.7")
    implementation("com.google.android.exoplayer:extension-mediasession:2.18.7")
    implementation("androidx.work:work-runtime:2.7.1")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.jsoup:jsoup:1.10.3")



    // ✅ USE THIS (latest Play Core Split module)
    implementation ("com.google.android.play:app-update:2.1.0")




    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.android.support:multidex:1.0.3")
    implementation("com.makeramen:roundedimageview:2.3.0")

    implementation("com.google.android.gms:play-services-ads:23.6.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.google.android.ump:user-messaging-platform:3.1.0")
    implementation("android.arch.lifecycle:extensions:1.1.1")

    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-config")

    //facebook ads
    implementation("com.facebook.android:audience-network-sdk:6.12.0")
    implementation("com.google.ads.mediation:facebook:6.12.0.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
}