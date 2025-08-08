package com.hdvideoplayer.smartplayer.player.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adapters.BottomMenuAdapter;
import com.hdvideoplayer.smartplayer.player.adapters.music.MusicInfoAdapter;
import com.hdvideoplayer.smartplayer.player.adsprosimple.AdManager;
import com.hdvideoplayer.smartplayer.player.data.database.MyDatabase;
import com.hdvideoplayer.smartplayer.player.data.datasource.MusicDatabaseControl;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicArtist;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicPlaylist;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.data.utils.MusicFavoriteUtil;
import com.hdvideoplayer.smartplayer.player.dialog.AddToPlaylistDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.BottomMenuDialogControl;
import com.hdvideoplayer.smartplayer.player.dialog.InputDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.MediaInfoDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.QuestionDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.SortDialogBuilder;
import com.hdvideoplayer.smartplayer.player.fragments.music.AddMusicPlaylistDialogFragment;
import com.hdvideoplayer.smartplayer.player.presenter.music.MusicDialogPresenter;
import com.hdvideoplayer.smartplayer.player.util.FirebaseAnalyticsUtils;
import com.hdvideoplayer.smartplayer.player.util.Utility;
import com.hdvideoplayer.smartplayer.player.view.MusicDialogView;

public class MusicPlaylistActivity extends AppCompatActivity implements MusicDialogView, MusicInfoAdapter.MusicInfoCallback {

    private ImageView ivAddMusic, ivViewMode, ivEmpty;
    private RelativeLayout rlSearchView, rlTitle;
    private ProgressBar loading;
    private RecyclerView rvMusicDialog;
    private SearchView searchView;
    private TextView tvFolderName;

    private MusicInfoAdapter mAdapter;
    private List<MusicInfo> mMusics;
    private MusicArtist mMusicArtist;
    private MusicPlaylist mPlaylist;
    private int mType;
    Context mContext;
    private boolean mIsNewPlaylist;
    private MusicInfo musicRequestDelete = null;
    private int positionMusicRequest = -1;
    MusicDialogPresenter mPresenter;
    ImageView iv_empty;

    public MusicPlaylistActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playlist);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, statusBarInsets.top, 0, 0); // Add padding for status bar
            return insets;
        });
        mContext = this;
        // Get data passed via Intent
        ivAddMusic = findViewById(R.id.iv_add_video);
        ivViewMode = findViewById(R.id.iv_view_mode);
        rlSearchView = findViewById(R.id.rl_search_view);
        rlTitle = findViewById(R.id.rl_title);
        loading = findViewById(R.id.loading);
        rvMusicDialog = findViewById(R.id.rv_video_dialog);
        searchView = findViewById(R.id.search_view);
        tvFolderName = findViewById(R.id.tv_folder_name);
        iv_empty = findViewById(R.id.iv_empty);

        RelativeLayout adContainerBanner = findViewById(R.id.adContainerBanner);
        ShimmerFrameLayout shimmerContainerBanner = findViewById(R.id.shimmer_container_banner);
        AdManager.showBanner(adContainerBanner, shimmerContainerBanner, MusicPlaylistActivity.this);


        mPresenter = new MusicDialogPresenter(this, new MusicDataRepository(mContext));

        // Set up RecyclerView
        rvMusicDialog.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MusicInfoAdapter(this, false, this, null);
        rvMusicDialog.setAdapter(mAdapter);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            mType = intent.getIntExtra("type", -1);
            mMusicArtist = (MusicArtist) intent.getSerializableExtra("music_artist");
            mPlaylist = (MusicPlaylist) intent.getSerializableExtra("music_playlist");
        }
        int i = mType;

        if (i == 2) {
            this.tvFolderName.setText(R.string.favorite);
            mPresenter.getAllFavoriteMusic();
        } else if (i == 3) {
            MusicArtist musicArtist = this.mMusicArtist;
            if (musicArtist != null) {
                this.tvFolderName.setText(musicArtist.getArtistName());
                mPresenter.getAllMusicOfArtist(this.mMusicArtist);
            }
        } else if (i == 1) {
            MusicPlaylist musicPlaylist = this.mPlaylist;
            if (musicPlaylist != null) {
                this.tvFolderName.setText(musicPlaylist.getPlaylistName());
                ((MusicDialogPresenter) this.mPresenter).getAllMusicOfPlaylist(this.mPlaylist);
            }
            this.ivAddMusic.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
        // Set listeners for buttons
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.iv_search).setOnClickListener(v -> showSearchView());
        findViewById(R.id.iv_sort).setOnClickListener(v -> showSortDialog());


        findViewById(R.id.tv_cancel_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSearchView.setVisibility(4);
                rlTitle.setVisibility(0);
                MusicInfoAdapter musicInfoAdapter = mAdapter;
                if (musicInfoAdapter != null) {
                    musicInfoAdapter.updateMusicDataList(mMusics);
                }
            }
        });

        findViewById(R.id.iv_add_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddVideoClick();
            }
        });

        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
               searchView.clearFocus();
                return false;
            }

            public boolean onQueryTextChange(String str) {
                List searchMusicByMusicName = Utility.searchMusicByMusicName(mMusics, str);
                if (mAdapter != null) {
                   mAdapter.updateMusicDataList(searchMusicByMusicName);
                }
                return false;
            }
        });
        if (this.mIsNewPlaylist) {
            onAddVideoClick();
        }
    }

    public void setIsNewPlaylist(boolean z) {
        this.mIsNewPlaylist = z;
    }


    public void onAddVideoClick() {
        new AddMusicPlaylistDialogFragment(this.mPlaylist, new AddMusicPlaylistDialogFragment.Callback() {
            @Override
            public void onDismiss() {
                if (mPresenter != null) {
                    ((MusicDialogPresenter) mPresenter).getAllMusicOfPlaylist(mPlaylist);
                }
            }
        }).show(getSupportFragmentManager().beginTransaction(), "dialog_playlist_add_music");
    }


    private void showSearchView() {
        rlSearchView.setVisibility(View.VISIBLE);
        rlTitle.setVisibility(View.GONE);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
    }

    private void showSortDialog() {
        BottomMenuDialogControl.getInstance().showSortDialogForMusic(mContext, new SortDialogBuilder.OkButtonClickListener() {
            public final void onClick(int i, boolean z) {
                MusicInfoAdapter musicInfoAdapter = mAdapter;
                if (musicInfoAdapter != null) {
                    musicInfoAdapter.sortMusicList(i, z);
                }
            }
        });
    }

    @Override
    public void onMoreClick(int position, MusicInfo musicInfo) {
        boolean checkFavoriteMusicIdExisted = MusicFavoriteUtil.checkFavoriteMusicIdExisted(this.mContext, musicInfo.getId());
        BottomMenuDialogControl.getInstance().showMoreDialogMusic(this.mContext, checkFavoriteMusicIdExisted, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                moreItemClick(musicInfo, checkFavoriteMusicIdExisted, i, i);
            }
        });
    }


    public void createNewPalyList(String str, String str2, MusicInfo musicInfo, int i, String str3) {
        final String trim = str3.trim();
        if (TextUtils.isEmpty(str3)) {
            Toast.makeText(this.mContext, R.string.empty_music_name, 0).show();
        } else if (!trim.equals(str)) {
            if (Build.VERSION.SDK_INT >= 30) {
                str = trim + str2;
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", str);
                Uri parse = Uri.parse(musicInfo.getUri());
                musicInfo.setDisplayName(str);
                ContentResolver contentResolver = this.mContext.getContentResolver();
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
            Utility.renameAMusic(this.mContext, musicInfo, trim + str2, new MediaScannerConnection.OnScanCompletedListener() {
                public final void onScanCompleted(String str, Uri uri) {
                    renameMusicRequest(musicInfo2, trim, str4, i2, str, uri);
                }
            });
        }
    }

    public void renameMusicRequest(MusicInfo musicInfo, String str, String str2, final int i, String str3, Uri uri) {
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

    public void moreItemClick(final MusicInfo musicInfo, boolean z, final int i, int i2) {
        String str = FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_MORE;
        if (i2 == 0) {
            MusicFavoriteUtil.addFavoriteMusicId(this.mContext, musicInfo.getId(), !z);
            if (z && this.mType == 2 && this.mPresenter != null) {
                ((MusicDialogPresenter) this.mPresenter).getAllFavoriteMusic();
            }
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_favorite");
        } else if (i2 == 1) {
            List allPlaylist = MyDatabase.getInstance(this.mContext).musicPlaylistDAO().getAllPlaylist();
            if (allPlaylist == null || allPlaylist.isEmpty()) {
                Toast.makeText(this.mContext, "you haven't created any playlist yet", 0).show();
            } else {
                new AddToPlaylistDialogBuilder(this.mContext, musicInfo.getId(), allPlaylist, new AddToPlaylistDialogBuilder.OkButtonClickListener() {
                    public final void onClose() {
                        if (mPresenter != null && mType == 1) {
                            ((MusicDialogPresenter) mPresenter).getAllMusicOfPlaylist(mPlaylist);
                        }
                    }
                }).build().show();
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
            int colors = Utility.getColorAttr(mContext, Utility.getColorAttr(mContext, R.color.app_color), R.color.color_787D85);
            new InputDialogBuilder(this.mContext, new InputDialogBuilder.OkButtonClickListener() {
                public final void onClick(String str) {
                    createNewPalyList(str4, str3, musicInfo2, i3, str);
                }
            }, displayName).setTitle(R.string.rename, colors).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_rename_music");
        } else if (i2 == 3) {
            Utility.shareMusic(this.mContext, musicInfo);
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_share_music");
        } else if (i2 == 4) {
            new MediaInfoDialogBuilder(this.mContext, musicInfo).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_info");
        } else if (i2 == 5) {
            new QuestionDialogBuilder(this.mContext, new QuestionDialogBuilder.OkButtonClickListener() {
                public void onCancelClick() {
                }

                public void onOkClick() {
                    Uri parse = Uri.parse(musicInfo.getUri());
                    if (parse != null) {
                        ContentResolver contentResolver = mContext.getContentResolver();
                        try {
                            // Attempt deletion
                            int deletedRows = contentResolver.delete(parse, null, null);
                            if (deletedRows > 0) {
                                // Successfully deleted
                                mAdapter.removeItemAtPosition(i);
                                MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                                MusicFavoriteUtil.addFavoriteMusicId(mContext, musicInfo.getId(), false);
                            } else {
                                // Handle the case where no rows were deleted (e.g., URI not found)
                                Log.e("Delete Error", "No rows deleted. URI might not exist: " + musicInfo.getUri());
                            }
                        } catch (SecurityException e) {
                            // Handle security exception based on API levels
                            handleSecurityException(contentResolver, parse, musicInfo, i, e);
                        }
                    } else {
                        Log.e("Delete Error", "Invalid URI: " + musicInfo.getUri());
                    }
                }
            }).setTitle(R.string.delete, this.mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_delete");
        }
    }

    private void handleSecurityException(ContentResolver contentResolver, Uri parse, MusicInfo musicInfo, int i, SecurityException e) {
        if (Build.VERSION.SDK_INT >= 30) {
            // Handle for API >= 30
            PendingIntent pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
            if (pendingIntent != null) {
                IntentSenderRequest build = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
                positionMusicRequest = i;
                musicRequestDelete = musicInfo;
                launcherDeleteMusic.launch(build);
            }
        } else if (Build.VERSION.SDK_INT < 29) {
            // Handle for API < 29 (pre-scoped storage)
            mAdapter.removeItemAtPosition(i);
            MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
            MusicFavoriteUtil.addFavoriteMusicId(mContext, musicInfo.getId(), false);
        } else {
            // For API levels between 29 and 30, handle RecoverableSecurityException
            if (e instanceof RecoverableSecurityException) {
                PendingIntent pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                if (pendingIntent != null) {
                    IntentSenderRequest build = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
                    positionMusicRequest = i;
                    musicRequestDelete = musicInfo;
                    launcherDeleteMusic.launch(build);
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

    public void handleLauncherDeleteMusic(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionMusicRequest;
            if (i >= 0 && i < this.mAdapter.getItemCount()) {
                this.mAdapter.removeItemAtPosition(this.positionMusicRequest);
            }
            if (this.musicRequestDelete != null) {
                MusicDatabaseControl.getInstance().removeMusicById(this.musicRequestDelete.getId());
                MusicFavoriteUtil.addFavoriteMusicId(this.mContext, this.musicRequestDelete.getId(), false);
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
                    ContentResolver contentResolver = this.mContext.getContentResolver();
                    try {
                        if (Build.VERSION.SDK_INT >= 30) {
                            contentResolver.update(parse, contentValues, null);
                            this.mAdapter.notifyItemChanged(this.positionMusicRequest);
                        }
                    } catch (SecurityException unused) {
                        Toast.makeText(this.mContext, R.string.an_error_occurred, 0).show();
                    }
                }
            }
        }
    }
    public void onMusicLoader(List<MusicInfo> list) {
        mMusics = new ArrayList<>(list);
        mAdapter.updateMusicDataList(list);

        if (list == null || list.isEmpty()) {
            iv_empty.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            rvMusicDialog.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            rvMusicDialog.setVisibility(View.VISIBLE);
        }
    }
}