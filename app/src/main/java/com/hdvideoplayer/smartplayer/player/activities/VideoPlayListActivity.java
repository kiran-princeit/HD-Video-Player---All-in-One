package com.hdvideoplayer.smartplayer.player.activities;

import static com.hdvideoplayer.smartplayer.player.MyApplication.isNetworkConnected;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adapters.BottomMenuAdapter;
import com.hdvideoplayer.smartplayer.player.adapters.video.VideoAdapter;
import com.hdvideoplayer.smartplayer.player.adsprosimple.AdManager;
import com.hdvideoplayer.smartplayer.player.data.database.MyDatabase;
import com.hdvideoplayer.smartplayer.player.data.datasource.VideoDatabaseControl;
import com.hdvideoplayer.smartplayer.player.data.entity.video.Playlist;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoFolder;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.VideoDataRepository;
import com.hdvideoplayer.smartplayer.player.data.utils.VideoFavoriteUtil;
import com.hdvideoplayer.smartplayer.player.dialog.AddToPlaylistDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.BottomMenuDialogControl;
import com.hdvideoplayer.smartplayer.player.dialog.InputDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.MediaInfoDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.QuestionDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.SortDialogBuilder;
import com.hdvideoplayer.smartplayer.player.fragments.video.AddVideoPlaylistDialogFragment;
import com.hdvideoplayer.smartplayer.player.presenter.video.VideoDialogPresenter;
import com.hdvideoplayer.smartplayer.player.util.FirebaseAnalyticsUtils;
import com.hdvideoplayer.smartplayer.player.util.Utility;
import com.hdvideoplayer.smartplayer.player.view.VideoDialogView;

public class VideoPlayListActivity extends AppCompatActivity implements VideoDialogView, VideoAdapter.Callback {
    private ImageView ivAddVideo, ivDone, ivSearch, ivViewMode;
    private ProgressBar loading;
    private RecyclerView rvVideoDialog;
    private RelativeLayout rlSearchView, rlTitle;
    private SearchView searchView;
    private TextView tvFolderName;
    private VideoAdapter videoAdapter;
    private Context mContext;
    private List<VideoInfo> mVideos = new ArrayList<>();
    private int viewMode = 1; // 1 for list view, 2 for grid view
    private VideoDialogPresenter mPresenter;
    private VideoFolder mVideoFolder;
    private Playlist mPlaylist;
    private int mType;
    private boolean mIsNewPlaylist = false;
    private boolean mIsSelectedMode = false;
    private VideoInfo videoDeleteRequest = null;
    private VideoInfo videoRenameRequest = null;
    private int positionVideoRequest = -1;
    ImageView iv_empty;
    private RelativeLayout adContainer;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_video_play_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, statusBarInsets.top, 0, 0); // Add padding for status bar
            return insets;
        });
        mContext = this;

        // Initialize views
        ivAddVideo = findViewById(R.id.iv_add_video);
        ivDone = findViewById(R.id.iv_done);
        ivSearch = findViewById(R.id.iv_search);
        ivViewMode = findViewById(R.id.iv_view_mode);
        loading = findViewById(R.id.loading);
        rlSearchView = findViewById(R.id.rl_search_view);
        rlTitle = findViewById(R.id.rl_title);
        rvVideoDialog = findViewById(R.id.rv_video_dialog);
        searchView = findViewById(R.id.search_view);
        tvFolderName = findViewById(R.id.tv_folder_name);
        iv_empty = findViewById(R.id.iv_empty);

        adContainer = findViewById(R.id.adContainer);
        shimmerFrameLayout = findViewById(R.id.shimmer_container_banner);
        loadBannerAd();

        findViewById(R.id.iv_back).setOnClickListener(view -> {
            finish();
        });

        // Initialize presenter
        mPresenter = new VideoDialogPresenter(mContext, this, new VideoDataRepository(mContext));

        // Set adapter
        videoAdapter = new VideoAdapter(this, mIsSelectedMode, this, null);
        rvVideoDialog.setAdapter(videoAdapter);

        // Set layout manager based on viewMode
        if (viewMode == 1) {
            rvVideoDialog.setLayoutManager(new LinearLayoutManager(this));
        } else {
            rvVideoDialog.setLayoutManager(new GridLayoutManager(this, 2));
        }

        // Handle actions
        ivAddVideo.setOnClickListener(v -> onAddVideoClick());
        ivDone.setOnClickListener(v -> finish());
        ivSearch.setOnClickListener(v -> toggleSearchView(true));
        ivViewMode.setOnClickListener(v -> toggleViewMode());


        // Handle search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<VideoInfo> filteredVideos = Utility.searchVideoByVideoName(mVideos, newText);
                videoAdapter.updateVideoDataList(filteredVideos);
                return false;
            }
        });

        findViewById(R.id.tv_cancel_search).setOnClickListener(view -> {
            this.rlSearchView.setVisibility(4);
            this.rlTitle.setVisibility(0);
            VideoAdapter videoAdapter = this.videoAdapter;
            if (videoAdapter != null) {
                videoAdapter.updateVideoDataList(this.mVideos);
            }
        });

        findViewById(R.id.iv_sort).setOnClickListener(view -> {
            BottomMenuDialogControl.getInstance().showSortDialogForVideo(this.mContext, new SortDialogBuilder.OkButtonClickListener() {
                public final void onClick(int i, boolean z) {
                    if (videoAdapter != null) {
                        videoAdapter.sortVideoList(i, z);
                    }
                }
            });
        });

        // Load data based on type
        loadData();
        if (this.mIsNewPlaylist) {
            onAddVideoClick();
        }

    }
    private void loadBannerAd() {
        if (adContainer != null && !isNetworkConnected(VideoPlayListActivity.this)) {
            adContainer.setVisibility(View.GONE);
            return;
        }

        // Call your method to show the ad, e.g., using AdMob or Facebook
        AdManager.showBanner(adContainer, shimmerFrameLayout, VideoPlayListActivity.this);
    }

    private void loadData() {
        mType = getIntent().getIntExtra("folder_type", 1);
        mVideoFolder = (VideoFolder) getIntent().getSerializableExtra("video_folder");
        mPlaylist = (Playlist) getIntent().getSerializableExtra("playlist");

        if (mType == 4) {
            tvFolderName.setText(R.string.recently_added);
            mPresenter.getAllRecentlyVideo();
        } else if (mType == 3) {
            tvFolderName.setText(R.string.favorite);
            mPresenter.getAllFavoriteVideo();
        } else if (mType == 1) {
            if (mVideoFolder != null) {
                tvFolderName.setText(mVideoFolder.getFolderName());
                mPresenter.getAllVideoOfFolder(mVideoFolder);
            }
            if (mIsSelectedMode) {
                this.ivDone.setVisibility(View.VISIBLE);
                this.ivSearch.setVisibility(View.INVISIBLE);
            }
        } else if (mType == 2) {
            if (mPlaylist != null) {
                tvFolderName.setText(mPlaylist.getPlaylistName());
                mPresenter.getAllVideoOfPlaylist(mPlaylist);
            }
            ivAddVideo.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    private void toggleSearchView(boolean show) {
        rlSearchView.setVisibility(show ? View.VISIBLE : View.GONE);
        rlTitle.setVisibility(show ? View.GONE : View.VISIBLE);
        this.searchView.setFocusable(true);
        this.searchView.setIconified(false);
        this.searchView.requestFocusFromTouch();
    }

    private void toggleViewMode() {
        if (viewMode == 1) {
            viewMode = 2;
            ivViewMode.setImageResource(R.drawable.ic_grid_view);
            rvVideoDialog.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            viewMode = 1;
            ivViewMode.setImageResource(R.drawable.ic_list_view);
            rvVideoDialog.setLayoutManager(new LinearLayoutManager(this));
        }
        videoAdapter.setViewMode(viewMode);
    }

    private void onAddVideoClick() {
        new AddVideoPlaylistDialogFragment(mPlaylist, () -> mPresenter.getAllVideoOfPlaylist(mPlaylist))
                .show(getSupportFragmentManager(), "dialog_playlist_add_video");
    }

    public void onVideoLoader(List<VideoInfo> list) {
        mVideos = new ArrayList<>(list);
        videoAdapter.updateVideoDataList(list);

        if (list == null || list.isEmpty()) {
            iv_empty.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            rvVideoDialog.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            rvVideoDialog.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMoreClick(int position, int action, VideoInfo videoInfo) {
        boolean checkFavoriteVideoIdExisted = VideoFavoriteUtil.checkFavoriteVideoIdExisted(this.mContext, videoInfo.getId());
        final VideoInfo videoInfo2 = videoInfo;
        final int i3 = action;
        final boolean z = checkFavoriteVideoIdExisted;
        final int i4 = action;
        BottomMenuDialogControl.getInstance().showMoreDialogVideo(this.mContext, checkFavoriteVideoIdExisted, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                moreitemclick(videoInfo2, i3, z, i4, i);
            }
        });
    }

    public void moreitemclick(final VideoInfo videoInfo, int i, boolean z, final int i2, int i3) {
        String str = FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_MORE;
        switch (i3) {
            case 0:
                VideoFavoriteUtil.addFavoriteVideoId(this.mContext, videoInfo.getId(), !z);
                if (z && this.mType == 3 && this.mPresenter != null) {
                    ((VideoDialogPresenter) this.mPresenter).getAllFavoriteVideo();
                }
                FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_favorite");
                return;
            case 1:
                List allPlaylist = MyDatabase.getInstance(this.mContext).videoPlaylistDAO().getAllPlaylist();
                if (allPlaylist == null || allPlaylist.isEmpty()) {
                    Toast.makeText(this.mContext, "you haven't created any playlist yet", 0).show();
                } else {
                    new AddToPlaylistDialogBuilder(this.mContext, videoInfo.getId(), allPlaylist, new AddToPlaylistDialogBuilder.OkButtonClickListener() {
                        public final void onClose() {
                            if (mPresenter != null && mType == 2) {
                                ((VideoDialogPresenter) mPresenter).getAllVideoOfPlaylist(mPlaylist);
                            }
                        }
                    }).build().show();
                }
                return;
            case 2:
                String substring;
                String displayName = videoInfo.getDisplayName();
                String str2 = ".";
                if (displayName.indexOf(str2) > 0) {
                    substring = displayName.substring(displayName.lastIndexOf(str2));
                    displayName = displayName.substring(0, displayName.lastIndexOf(str2));
                } else {
                    substring = "";
                }
                final String str3 = substring;
                final String str4 = displayName;
                final VideoInfo videoInfo2 = videoInfo;
                final int i4 = i2;

                int colors = Utility.getColorAttr(mContext, Utility.getColorAttr(mContext, R.color.app_color), R.color.color_787D85);
                new InputDialogBuilder(this.mContext, new InputDialogBuilder.OkButtonClickListener() {
                    public final void onClick(String str) {
                        renameVideo(str4, str3, videoInfo2, i4, str);
                    }
                }, displayName).setTitle(R.string.rename, colors).build().show();
                FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_rename_video");
                return;
            case 3:
                Utility.shareVideo(this.mContext, videoInfo);
                FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_share_video");
                return;
            case 4:
                new MediaInfoDialogBuilder(this.mContext, videoInfo).build().show();
                FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_info");
                return;
            case 5:
                new QuestionDialogBuilder(this.mContext, new QuestionDialogBuilder.OkButtonClickListener() {
                    public void onCancelClick() {
                    }

                    public void onOkClick() {
                        Uri parse = Uri.parse(videoInfo.getUri());
                        ContentResolver contentResolver = mContext.getContentResolver();
                        PendingIntent pendingIntent = null;
                        try {
                            contentResolver.delete(parse, null, null);
                            videoAdapter.removeItemPosition(i2);
                            VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
                            VideoFavoriteUtil.addFavoriteVideoId(mContext, videoInfo.getId(), false);
                        } catch (SecurityException e) {
                            if (Build.VERSION.SDK_INT >= 30) {
                                pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
                            } else if (Build.VERSION.SDK_INT < 29) {
                                videoAdapter.removeItemPosition(i2);
                                VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
                                VideoFavoriteUtil.addFavoriteVideoId(mContext, videoInfo.getId(), false);
                            } else {
                                pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                            }
                            if (pendingIntent != null) {
                                IntentSenderRequest build = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
                                positionVideoRequest = i2;
                                videoDeleteRequest = videoInfo;
                                launcherDeleteVideo.launch(build);
                            }
                        }
                    }
                }).setTitle(R.string.delete, this.mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
                FirebaseAnalyticsUtils.putEventClick(this.mContext, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_MORE, "click_item_delete");
                return;
            default:
                return;
        }
    }

    public void renameVideo(String str, String str2, VideoInfo videoInfo, int i, String str3) {
        final String trim = str3.trim();
        if (TextUtils.isEmpty(str3)) {
            Toast.makeText(this.mContext, R.string.empty_video_name, 0).show();
        } else if (!trim.equals(str)) {
            if (Build.VERSION.SDK_INT >= 30) {
                str = trim + str2;
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", str);
                Uri parse = Uri.parse(videoInfo.getUri());
                videoInfo.setDisplayName(str);
                ContentResolver contentResolver = this.mContext.getContentResolver();
                try {
                    contentResolver.update(parse, contentValues, null);
                    this.videoAdapter.notifyItemChanged(i);
                    return;
                } catch (SecurityException unused) {
                    IntentSenderRequest build = new IntentSenderRequest.Builder(MediaStore.createWriteRequest(contentResolver, Collections.singletonList(parse)).getIntentSender()).build();
                    positionVideoRequest = i;
                    videoRenameRequest = videoInfo;
                    launcherRenameVideo.launch(build);
                    return;
                }
            }
            final VideoInfo videoInfo2 = videoInfo;
            final String str4 = str2;
            final int i2 = i;
            Utility.renameAVideo(this.mContext, videoInfo, trim + str2, new MediaScannerConnection.OnScanCompletedListener() {
                public final void onScanCompleted(String str, Uri uri) {
                    handlerequest(videoInfo2, trim, str4, i2, str, uri);
                }
            });
        }
    }

    public void handlerequest(VideoInfo videoInfo, String str, String str2, final int i, String str3, Uri uri) {
        if (uri != null) {
            long parseId;
            videoInfo.setDisplayName(str + str2);
            videoInfo.setPath(str3);
            videoInfo.setUri(uri.toString());
            try {
                parseId = ContentUris.parseId(uri);
            } catch (Exception unused) {
                parseId = 0;
            }
            VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
            videoInfo.setId(parseId);
            VideoDatabaseControl.getInstance().addVideo(videoInfo);
            Log.d("aaa", " renameAVideo = " + videoInfo.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public final void run() {
                    videoAdapter.notifyItemChanged(i);
                }
            });
        }
    }

    public void handlelauncherDeleteMusic(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionVideoRequest;
            if (i >= 0 && i < this.videoAdapter.getItemCount()) {
                this.videoAdapter.removeItemPosition(this.positionVideoRequest);
            }
            if (this.videoDeleteRequest != null) {
                VideoDatabaseControl.getInstance().removeVideoById(this.videoDeleteRequest.getId());
                VideoFavoriteUtil.addFavoriteVideoId(this.mContext, this.videoDeleteRequest.getId(), false);
            }
        }
    }

    public void handlelauncherRenameMusic(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionVideoRequest;
            if (i >= 0 && i < this.videoAdapter.getItemCount() && this.videoRenameRequest != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", this.videoRenameRequest.getDisplayName());
                Uri parse = Uri.parse(this.videoRenameRequest.getUri());
                ContentResolver contentResolver = this.mContext.getContentResolver();
                try {
                    if (Build.VERSION.SDK_INT >= 30) {
                        contentResolver.update(parse, contentValues, null);
                        this.videoAdapter.notifyItemChanged(this.positionVideoRequest);
                    }
                } catch (SecurityException unused) {
                    Toast.makeText(this.mContext, R.string.an_error_occurred, 0).show();
                }
            }
        }
    }

    private final ActivityResultLauncher<IntentSenderRequest> launcherDeleteVideo = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            handlelauncherDeleteMusic((ActivityResult) obj);
        }
    });
    private final ActivityResultLauncher<IntentSenderRequest> launcherRenameVideo = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            handlelauncherRenameMusic((ActivityResult) obj);
        }
    });
}