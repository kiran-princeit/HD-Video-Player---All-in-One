package hd.video.player.videoplayer.mplayer.masterplayer.activities;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.Const;
import hd.video.player.videoplayer.mplayer.masterplayer.Pref;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;

public class SplashActivity extends BaseActivity {


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        Utility.initFolderPath(this);
        new Handler().postDelayed(() -> {
            inNext();
        }, 2000);


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
    }

    private void inNext() {
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
    }

}
