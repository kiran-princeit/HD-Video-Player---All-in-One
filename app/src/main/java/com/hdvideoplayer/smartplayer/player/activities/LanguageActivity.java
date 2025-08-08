package com.hdvideoplayer.smartplayer.player.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hdvideoplayer.smartplayer.player.Const;
import com.hdvideoplayer.smartplayer.player.Pref;
import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adapters.LanguageAdapter;
import com.hdvideoplayer.smartplayer.player.model.Language;
import com.hdvideoplayer.smartplayer.player.util.LanguagePreference;
import com.hdvideoplayer.smartplayer.player.util.LocaleHelper;

import java.util.ArrayList;
import java.util.List;


public final class LanguageActivity extends BaseActivity {
    private LanguageAdapter languageAdapter;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_language);

        List<Language> languages = new ArrayList<>();
        languages.add(new Language("en", "English"));
        languages.add(new Language("es", "Spanish"));
        languages.add(new Language("de", "German"));
        languages.add(new Language("zh", "Chinese"));
        languages.add(new Language("fr", "French"));
        languages.add(new Language("hi", "Hindi"));
        languages.add(new Language("it", "Italian"));
        languages.add(new Language("ja", "Japanese"));
        languages.add(new Language("ru", "Russian"));

        RecyclerView recyclerView = findViewById(R.id.languageRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        languageAdapter = new LanguageAdapter(languages);
        recyclerView.setAdapter(languageAdapter);

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedLanguageCode = languageAdapter.getSelectedLanguageCode();
                if (selectedLanguageCode != null && !selectedLanguageCode.isEmpty()) {
                    LanguagePreference.saveLanguage(LanguageActivity.this, selectedLanguageCode);
                    LocaleHelper.setLocale(LanguageActivity.this, selectedLanguageCode);
                    Pref.getInstance().setString(Const.NEXT, "language");
                    Intent intent = new Intent(LanguageActivity.this, PermissionActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LanguageActivity.this, R.string.please_select_a_language, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
