package hd.video.player.videoplayer.mplayer.masterplayer.fragments;

import static hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.GlobalVar.appData;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.activities.LanguageActivity;
import hd.video.player.videoplayer.mplayer.masterplayer.util.LanguagePreference;

public class SettingFragment extends Fragment {
    private Context mContext;

    private long mLastClickTime = 0;
    TextView tvLanguageName;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_setting, viewGroup, false);

        tvLanguageName = inflate.findViewById(R.id.tvLanguageName);
        String selectedLanguageCode = LanguagePreference.getLanguage(getActivity());
        String languageName = getLanguageName(selectedLanguageCode);
        tvLanguageName.setText(languageName);

        inflate.findViewById(R.id.llLanguage).setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), LanguageActivity.class));
            getActivity().finish();

        });

        inflate.findViewById(R.id.llPrivacyPolicy).setOnClickListener(view -> {
            String privacyUrl = appData.getprivacyurl();
            openPrivacyPolicy(privacyUrl);

        });


        inflate.findViewById(R.id.llRateUs).setOnClickListener(view -> {
            final String appName = getActivity().getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="
                                + appName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id="
                                + appName)));
            }
        });

        inflate.findViewById(R.id.llShare).setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent ishare = new Intent(Intent.ACTION_SEND);
            ishare.setType("text/plain");
            ishare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
            startActivity(ishare);
        });

        inflate.findViewById(R.id.llAboutApp).setOnClickListener(view -> {
            infoDialog();
        });


        return inflate;
    }

    private void openPrivacyPolicy(String url) {
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "URL is null or empty", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLanguageName(String languageCode) {
        switch (languageCode) {
            case "en":
                return "English";
            case "es":
                return "Spanish";
            case "de":
                return "German";
            case "zh":
                return "Chinese";
            case "fr":
                return "French";
            case "hi":
                return "Hindi";
            case "it":
                return "Italian";
            case "ja":
                return "Japanese";
            case "ru":
                return "Russian";

            default:
                return "English"; // Default language
        }
    }


    private void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_about, null);
        builder.setView(dialogView);
        AlertDialog infodialog = builder.create();
        infodialog.setCancelable(false);
        infodialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txt_app_version = dialogView.findViewById(R.id.txt_app_version);
        Button tv_ok = dialogView.findViewById(R.id.tv_dialog_ok);

        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            Log.d("AppVersion", "Version Name: " + versionName);
            Log.d("AppVersion", "Version Code: " + versionCode);
            txt_app_version.setText(versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        tv_ok.setOnClickListener(view -> {
            infodialog.cancel();
        });


        Window window = infodialog.getWindow();
        if (window != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85); // 85% of screen width
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }
        infodialog.show();

    }


}
