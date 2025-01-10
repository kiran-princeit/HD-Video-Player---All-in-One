package hd.video.player.videoplayer.mplayer.masterplayer.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import hd.video.player.videoplayer.mplayer.masterplayer.util.LanguagePreference;
import hd.video.player.videoplayer.mplayer.masterplayer.util.LocaleHelper;

public abstract class BaseActivity extends  AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String languageCode = LanguagePreference.getLanguage(newBase);
        Context context = LocaleHelper.setLocale(newBase, languageCode);
        super.attachBaseContext(context);
    }


}
