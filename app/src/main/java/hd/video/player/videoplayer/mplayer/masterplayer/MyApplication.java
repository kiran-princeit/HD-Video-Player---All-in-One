package hd.video.player.videoplayer.mplayer.masterplayer;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;;
import androidx.multidex.MultiDex;

import java.util.Locale;

import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.SettingPreferences;


//public class MyApplication extends Application {
//
//    public void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//    }
//
//    public void onCreate() {
//        safedk_MyApplication_onCreate_a55696e7cb0eccf7226156540f6a95f1(this);
//    }
//
//    public void safedk_MyApplication_onCreate_a55696e7cb0eccf7226156540f6a95f1(MyApplication p0) {
//        super.onCreate();
//        Pref pref = new Pref(p0);
//        Pref.getInstance().init(p0);
//        switch (new SettingPreferences(p0.getBaseContext()).getLanguage()) {
//            case 1:
//                p0.setLanguageApp("en");
//                break;
//            case 2:
//                p0.setLanguageApp("zu");
//                break;
//            case 3:
//                p0.setLanguageApp("bn");
//                break;
//            case 4:
//                p0.setLanguageApp("es");
//                break;
//            case 5:
//                p0.setLanguageApp("hi");
//                break;
//            case 6:
//                p0.setLanguageApp("in");
//                break;
//            case 7:
//                p0.setLanguageApp("ira");
//                break;
//            case 8:
//                p0.setLanguageApp("phi");
//                break;
//            case 9:
//                p0.setLanguageApp("pt");
//                break;
//            case 10:
//                p0.setLanguageApp("ur");
//                break;
//            case 11:
//                p0.setLanguageApp("vi");
//                break;
//            default:
//                p0.setLanguageApp(Resources.getSystem().getConfiguration().locale.getLanguage());
//                break;
//        }
//    }
//
//    public void setLanguageApp(String str) {
//        Configuration configuration = getBaseContext().getResources().getConfiguration();
//        if (!configuration.locale.getLanguage().equals(str)) {
//            Locale locale = new Locale(str);
//            Locale.setDefault(locale);
//            Configuration configuration2 = new Configuration(configuration);
//            configuration2.locale = locale;
//            getBaseContext().getResources().updateConfiguration(configuration2, getBaseContext().getResources().getDisplayMetrics());
//        }
//    }
//}
public class MyApplication extends Application {

    @Override
    public void attachBaseContext(Context baseContext) {
        super.attachBaseContext(baseContext);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeApplicationSettings(this);
        MultiDex.install(this);
    }

    public void initializeApplicationSettings(MyApplication applicationInstance) {

        Pref preferences = new Pref(applicationInstance);
        preferences.getInstance().init(applicationInstance);

        SettingPreferences settingPreferences = new SettingPreferences(applicationInstance.getBaseContext());
        int selectedLanguage = settingPreferences.getLanguage();

        switch (selectedLanguage) {
            case 1:
                applicationInstance.updateAppLanguage("en"); // English
                break;
            case 2:
                applicationInstance.updateAppLanguage("zu"); // Zulu
                break;
            case 3:
                applicationInstance.updateAppLanguage("bn"); // Bengali
                break;
            case 4:
                applicationInstance.updateAppLanguage("es"); // Spanish
                break;
            case 5:
                applicationInstance.updateAppLanguage("hi"); // Hindi
                break;
            case 6:
                applicationInstance.updateAppLanguage("in"); // Indonesian
                break;
            case 7:
                applicationInstance.updateAppLanguage("ira"); // Farsi
                break;
            case 8:
                applicationInstance.updateAppLanguage("phi"); // Filipino
                break;
            case 9:
                applicationInstance.updateAppLanguage("pt"); // Portuguese
                break;
            case 10:
                applicationInstance.updateAppLanguage("ur"); // Urdu
                break;
            case 11:
                applicationInstance.updateAppLanguage("vi"); // Vietnamese
                break;
            default:
                applicationInstance.updateAppLanguage(Resources.getSystem().getConfiguration().locale.getLanguage());
                break;
        }
    }

    public void updateAppLanguage(String languageCode) {
        Configuration appConfig = getBaseContext().getResources().getConfiguration();
        if (!appConfig.locale.getLanguage().equals(languageCode)) {
            Locale newLocale = new Locale(languageCode);
            Locale.setDefault(newLocale);

            Configuration updatedConfig = new Configuration(appConfig);
            updatedConfig.locale = newLocale;

            getBaseContext().getResources().updateConfiguration(updatedConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }
}


