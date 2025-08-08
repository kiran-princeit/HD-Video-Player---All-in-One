package com.hdvideoplayer.smartplayer.player.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.MyApplication;
import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adapters.BottomMenuAdapter;
import com.hdvideoplayer.smartplayer.player.adapters.music.MusicInfoAdapter;
import com.hdvideoplayer.smartplayer.player.adsprosimple.AdManager;
import com.hdvideoplayer.smartplayer.player.data.database.MyDatabase;
import com.hdvideoplayer.smartplayer.player.data.datasource.MusicDatabaseControl;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicAlbum;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.data.utils.MusicFavoriteUtil;
import com.hdvideoplayer.smartplayer.player.dialog.AddToPlaylistDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.BottomMenuDialogControl;
import com.hdvideoplayer.smartplayer.player.dialog.InputDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.MediaInfoDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.QuestionDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.SortDialogBuilder;

import com.hdvideoplayer.smartplayer.player.presenter.music.MusicAlbumPresenter;
import com.hdvideoplayer.smartplayer.player.util.FirebaseAnalyticsUtils;
import com.hdvideoplayer.smartplayer.player.util.Utility;
import com.hdvideoplayer.smartplayer.player.util.constant.AppConstant;
import com.hdvideoplayer.smartplayer.player.view.music.MusicAlbumView;

public class MusicAlbumActivity extends AppCompatActivity implements MusicAlbumView, MusicInfoAdapter.MusicInfoCallback {

    private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
    private ImageView ivAlbumArt;
    private MusicInfoAdapter mAdapter;
    private List<MusicInfo> mMusics = new ArrayList<>();
    private MusicAlbum mMusicAlbum;
    private RelativeLayout rlSearchView, rlTitle, adContainer;
    private RecyclerView rvContent;
    private SearchView searchView;
    private TextView tvArtist, tvInfoAlbum, tvTitle, tvTitle2;
    private ShimmerFrameLayout shimmerFrameLayout;
    private MusicInfo musicRequestDelete = null;
    private int positionMusicRequest = -1;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_album);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mContext = this;

        // Retrieve the passed MusicAlbum object
        mMusicAlbum = (MusicAlbum) getIntent().getSerializableExtra("EXTRA_MUSIC_ALBUM");

        if (mMusicAlbum == null) {
            finish();
            return;
        }

        initView();
        setupListeners();
        loadBannerAd();
        loadMusicData();
    }

    private void initView() {
        adContainer = findViewById(R.id.adContainerBanner);
        shimmerFrameLayout = findViewById(R.id.shimmer_container_banner);
        ivAlbumArt = findViewById(R.id.iv_album_art);
        rlSearchView = findViewById(R.id.rl_search_view);
        rlTitle = findViewById(R.id.rl_title);
        rvContent = findViewById(R.id.rv_content);
        searchView = findViewById(R.id.search_view);
        tvArtist = findViewById(R.id.tv_artist);
        tvInfoAlbum = findViewById(R.id.tv_info_album);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle2 = findViewById(R.id.tv_title_2);

        tvTitle.setText(mMusicAlbum.getAlbumName());
        tvTitle2.setText(mMusicAlbum.getAlbumName());
        tvArtist.setText(mMusicAlbum.getArtistName());
        Glide.with(this)
                .load(ContentUris.withAppendedId(artworkUri, mMusicAlbum.getAlbumId()))
                .placeholder(R.drawable.ic_music_album)
                .error(R.drawable.ic_music_album)
                .centerCrop()
                .into(ivAlbumArt);

        mAdapter = new MusicInfoAdapter(this, false, this, null);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(mAdapter);
    }

    private void setupListeners() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        findViewById(R.id.tv_cancel_search).setOnClickListener(v -> {
            rlTitle.setVisibility(View.VISIBLE);
            rlSearchView.setVisibility(View.GONE);
            if (mAdapter != null) {
                mAdapter.updateMusicDataList(mMusics);
            }
        });

        findViewById(R.id.iv_search).setOnClickListener(v -> {
            rlSearchView.setVisibility(View.VISIBLE);
            rlTitle.setVisibility(View.INVISIBLE);
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
        });

        findViewById(R.id.ll_play).setOnClickListener(v -> {
            AdManager.showInterstitial(this, () -> {
                if (mMusics != null && !mMusics.isEmpty()) {
                    Intent intent = new Intent(this, PlayMusicActivity.class);
                    intent.putExtra(AppConstant.IntentExtra.EXTRA_MUSIC_NUMBER, 0);
                    intent.putExtra(AppConstant.IntentExtra.EXTRA_MUSIC_ARRAY, new ArrayList<>(getAllMusicId()));
                    startActivity(intent);
                }
            });
        });


        findViewById(R.id.iv_sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomMenuDialogControl.getInstance().showSortDialogForMusic(mContext, new SortDialogBuilder.OkButtonClickListener() {
                    public final void onClick(int i, boolean z) {
                        mAdapter.sortMusicList(i, z);
                    }
                });
            }
        });
    }

    private void loadBannerAd() {
        if (adContainer != null && !MyApplication.isNetworkConnected(this)) {
            adContainer.setVisibility(View.GONE);
        } else {
            AdManager.showBanner(adContainer, shimmerFrameLayout, this);
        }
    }

    private void loadMusicData() {
        MusicAlbumPresenter presenter = new MusicAlbumPresenter(this, this, new MusicDataRepository(this));
        presenter.loadMusicListForAlbum(mMusicAlbum);
    }

    private List<Long> getAllMusicId() {
        List<Long> ids = new ArrayList<>();
        if (mMusics != null) {
            for (MusicInfo music : mMusics) {
                ids.add(music.getId());
            }
        }
        return ids;
    }

//    @Override
//    public void onUpdateMusicList(List<MusicInfo> musicList) {
//        mMusics = musicList;
//        if (mAdapter != null) {
//            mAdapter.updateMusicDataList(musicList);
//        }
//
//        long totalTime = 0;
//        for (MusicInfo music : musicList) {
//            totalTime += music.getDuration();
//        }
//
//        totalTime /= 60000;
//        tvInfoAlbum.setText(getString(R.string.album_info, mMusicAlbum.getNumberOfSongs(), totalTime));
//    }


    @Override
    public void onUpdateAlbum(List<MusicAlbum> list) {

    }

    public void onUpdateMusicList(List<MusicInfo> list) {
        if (this.mMusicAlbum != null) {
            this.mMusics = list;
            MusicInfoAdapter musicInfoAdapter = this.mAdapter;
            if (musicInfoAdapter != null) {
                musicInfoAdapter.updateMusicDataList(list);
            }
            long j = 0;
            for (MusicInfo duration : list) {
                j += duration.getDuration();
            }
            j /= 60000;
            long lastYear = this.mMusicAlbum.getLastYear();
            Log.d("aaa ", "last year = " + lastYear + ", totalTime = " + j);
            if (lastYear > 0) {
                this.tvInfoAlbum.setText(MusicAlbumActivity.this.getResources().getQuantityString(R.plurals.value_of_album_info, (int) this.mMusicAlbum.getNumberOfSongs(), new Object[]{Long.valueOf(this.mMusicAlbum.getLastYear()), Long.valueOf(this.mMusicAlbum.getNumberOfSongs()), Long.valueOf(j)}));
            } else {
                this.tvInfoAlbum.setText(MusicAlbumActivity.this.getResources().getQuantityString(R.plurals.value_of_album_info_2, (int) this.mMusicAlbum.getNumberOfSongs(), new Object[]{Long.valueOf(this.mMusicAlbum.getNumberOfSongs()), Long.valueOf(j)}));
            }
        }
    }


    @Override
    public void onMoreClick(int i, final MusicInfo musicInfo) {
        final boolean checkFavoriteMusicIdExisted = MusicFavoriteUtil.checkFavoriteMusicIdExisted(MusicAlbumActivity.this, musicInfo.getId());
        BottomMenuDialogControl.getInstance().showMoreDialogMusic(MusicAlbumActivity.this, checkFavoriteMusicIdExisted, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                moreitemclick(musicInfo, checkFavoriteMusicIdExisted, i, i);
            }
        });
    }

    public void renameclick(String str, String str2, MusicInfo musicInfo, int i, String str3) {
        final String trim = str3.trim();
        if (TextUtils.isEmpty(str3)) {
            Toast.makeText(MusicAlbumActivity.this, R.string.empty_music_name, 0).show();
        } else if (!trim.equals(str)) {
            if (Build.VERSION.SDK_INT >= 30) {
                str = trim + str2;
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", str);
                Uri parse = Uri.parse(musicInfo.getUri());
                musicInfo.setDisplayName(str);
                ContentResolver contentResolver = MusicAlbumActivity.this.getContentResolver();
                try {
                    contentResolver.update(parse, contentValues, null);
                    this.mAdapter.notifyItemChanged(i);
                    return;
                } catch (SecurityException unused) {
                    IntentSenderRequest build = new IntentSenderRequest.Builder(MediaStore.createWriteRequest(contentResolver, Collections.singletonList(parse)).getIntentSender()).build();
                    this.positionMusicRequest = i;
                    this.launcherRenameMusic.launch(build);
                    return;
                }
            }
            final MusicInfo musicInfo2 = musicInfo;
            final String str4 = str2;
            final int i2 = i;
            Utility.renameAMusic(MusicAlbumActivity.this, musicInfo, trim + str2, new MediaScannerConnection.OnScanCompletedListener() {
                public final void onScanCompleted(String str, Uri uri) {
                    handlerequest(musicInfo2, trim, str4, i2, str, uri);
                }
            });
        }
    }

    public void handlerequest(MusicInfo musicInfo, String str, String str2, final int i, String str3, Uri uri) {
        if (uri != null) {
            long parseId;
            musicInfo.setDisplayName(str + str2);
            musicInfo.setPath(str3);
            musicInfo.setUri(uri.toString());
            try {
                parseId = ContentUris.parseId(uri);
            } catch (Exception unused) {
                parseId = 0;
            }
            MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
            musicInfo.setId(parseId);
            MusicDatabaseControl.getInstance().addMusic(musicInfo);
            Log.d("aaa", " renameAMusic = " + musicInfo.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public final void run() {
                    mAdapter.notifyItemChanged(i);
                }
            });
        }
    }

    public void moreitemclick(final MusicInfo musicInfo, boolean z, final int i, int i2) {
        String str = FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_MORE;
        if (i2 == 0) {
            MusicFavoriteUtil.addFavoriteMusicId(MusicAlbumActivity.this, musicInfo.getId(), !z);

            FirebaseAnalyticsUtils.putEventClick(MusicAlbumActivity.this, str, "click_item_favorite");
        } else if (i2 == 1) {
            List allPlaylist = MyDatabase.getInstance(MusicAlbumActivity.this).musicPlaylistDAO().getAllPlaylist();
            if (allPlaylist == null || allPlaylist.isEmpty()) {
                Toast.makeText(MusicAlbumActivity.this, "you haven't created any playlist yet", 0).show();
            } else {
                new AddToPlaylistDialogBuilder(MusicAlbumActivity.this, musicInfo.getId(), allPlaylist, null).build().show();
            }
        } else if (i2 == 2) {
            String substring;
            String displayName = musicInfo.getDisplayName();
            String str2 = ".";
            if (displayName.indexOf(str2) > 0) {
                substring = displayName.substring(displayName.lastIndexOf(str2));
                displayName = displayName.substring(0, displayName.lastIndexOf(str2));
            } else {
                substring = "";
            }
            final String str3 = substring;
            final String str4 = displayName;
            final MusicInfo musicInfo2 = musicInfo;
            final int i3 = i;
            int colors = Utility.getColorAttr(getApplicationContext(), Utility.getColorAttr(getApplicationContext(), R.color.app_color), R.color.color_787D85);
            new InputDialogBuilder(MusicAlbumActivity.this, new InputDialogBuilder.OkButtonClickListener() {
                public final void onClick(String str) {
                    renameclick(str4, str3, musicInfo2, i3, str);
                }
            }, displayName).setTitle(R.string.rename, colors).build().show();
            FirebaseAnalyticsUtils.putEventClick(MusicAlbumActivity.this, str, "click_rename_music");
        } else if (i2 == 3) {
            Utility.shareMusic(MusicAlbumActivity.this, musicInfo);
            FirebaseAnalyticsUtils.putEventClick(MusicAlbumActivity.this, str, "click_share_music");
        } else if (i2 == 4) {
            new MediaInfoDialogBuilder(MusicAlbumActivity.this, musicInfo).build().show();
            FirebaseAnalyticsUtils.putEventClick(MusicAlbumActivity.this, str, "click_item_info");
        } else if (i2 == 5) {
            new QuestionDialogBuilder(MusicAlbumActivity.this, new QuestionDialogBuilder.OkButtonClickListener() {
                public void onCancelClick() {
                }

                public void onOkClick() {
                    Uri parse = Uri.parse(musicInfo.getUri());
                    ContentResolver contentResolver = getContentResolver();
                    PendingIntent pendingIntent = null;
                    try {
                        contentResolver.delete(parse, null, null);
                        mAdapter.removeItemAtPosition(i);
                        MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                        MusicFavoriteUtil.addFavoriteMusicId(getApplicationContext(), musicInfo.getId(), false);
                    } catch (SecurityException e) {
                        if (Build.VERSION.SDK_INT >= 30) {
                            pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
                        } else if (Build.VERSION.SDK_INT < 29) {
                            mAdapter.removeItemAtPosition(i);
                            MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                            MusicFavoriteUtil.addFavoriteMusicId(getApplicationContext(), musicInfo.getId(), false);
                        } else {
                            pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                        }
                        if (pendingIntent != null) {
                            IntentSenderRequest build = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
                            positionMusicRequest = i;
                            musicRequestDelete = musicInfo;
                            launcherDeleteMusic.launch(build);
                        }
                    }
                }
            }).setTitle(R.string.delete, MusicAlbumActivity.this.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
            FirebaseAnalyticsUtils.putEventClick(MusicAlbumActivity.this, str, "click_item_delete");
        }
    }

    public void handleLauncherDeleteMusic(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionMusicRequest;
            if (i >= 0 && i < this.mAdapter.getItemCount()) {
                this.mAdapter.removeItemAtPosition(this.positionMusicRequest);
            }
            if (this.musicRequestDelete != null) {
                MusicDatabaseControl.getInstance().removeMusicById(this.musicRequestDelete.getId());
                MusicFavoriteUtil.addFavoriteMusicId(MusicAlbumActivity.this, this.musicRequestDelete.getId(), false);
            }
        }
    }

    public void handleLauncherRenameMusic(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionMusicRequest;
            if (i >= 0 && i < this.mAdapter.getItemCount()) {
                MusicInfo item = this.mAdapter.getItem(this.positionMusicRequest);
                if (item != null) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_display_name", item.getDisplayName());
                    Uri parse = Uri.parse(item.getUri());
                    ContentResolver contentResolver = MusicAlbumActivity.this.getContentResolver();
                    try {
                        if (Build.VERSION.SDK_INT >= 30) {
                            contentResolver.update(parse, contentValues, null);
                            this.mAdapter.notifyItemChanged(this.positionMusicRequest);
                        }
                    } catch (SecurityException unused) {
                        Toast.makeText(MusicAlbumActivity.this, R.string.an_error_occurred, 0).show();
                    }
                }
            }
        }
    }

    private final ActivityResultLauncher<IntentSenderRequest> launcherDeleteMusic = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            handleLauncherDeleteMusic((ActivityResult) obj);
        }
    });
    private final ActivityResultLauncher<IntentSenderRequest> launcherRenameMusic = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            handleLauncherRenameMusic((ActivityResult) obj);
        }
    });
}