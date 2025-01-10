package hd.video.player.videoplayer.mplayer.masterplayer.fragments.video;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.IntentSenderRequest.Builder;
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import hd.video.player.videoplayer.mplayer.masterplayer.R;

import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.video.VideoAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.data.dao.video.VideoPlaylistDAO;
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.VideoDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoFolder;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.VideoDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.SettingPreferences;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.VideoFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.AddToPlaylistDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.InputDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MediaInfoDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.SortDialogBuilder.OkButtonClickListener;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseDialogFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.video.VideoDialogPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.view.VideoDialogView;
import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant;

public class VideoPlaylistDialogFragment extends BaseDialogFragment<VideoDialogPresenter> implements VideoDialogView, VideoAdapter.Callback {
    ImageView ivAddVideo;
    ImageView ivDone;
    ImageView ivSearch;
    private int viewMode = 1;
    ImageView ivViewMode;
    private final ActivityResultLauncher<IntentSenderRequest> launcherDeleteVideo = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            VideoPlaylistDialogFragment.this.handlelauncherDeleteMusic((ActivityResult) obj);
        }
    });
    private final ActivityResultLauncher<IntentSenderRequest> launcherRenameVideo = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            VideoPlaylistDialogFragment.this.handlelauncherRenameMusic((ActivityResult) obj);
        }
    });
    ProgressBar loading;
    private VideoAdapter videoAdapter;
    private Context mContext;
    private boolean mIsNewPlaylist = false;
    private boolean mIsSelectedMode = false;
    private Playlist mPlaylist;
    private Callback mPlaylistCallback;
    private Playlist mPlaylistSelected;
    private int mType;
    private VideoFolder mVideoFolder;
    private List<VideoInfo> mVideos = new ArrayList();
    private int positionVideoRequest = -1;
    RelativeLayout rlSearchView;
    RelativeLayout rlTitle;
    RecyclerView rvVideoDialog;
    SearchView searchView;
    TextView tvFolderName;
    private VideoInfo videoDeleteRequest = null;
    private VideoInfo videoRenameRequest = null;

    public interface Callback {
        void onDialogDismiss();
    }

    public VideoPlaylistDialogFragment(int i, VideoFolder videoFolder) {
        this.mType = i;
        this.mVideoFolder = videoFolder;
    }

    public VideoPlaylistDialogFragment(int i) {
        this.mType = i;
    }

    public VideoPlaylistDialogFragment() {
        // doesn't do anything special
    }

    public VideoPlaylistDialogFragment(int i, Playlist playlist, Callback callback) {
        this.mType = i;
        this.mPlaylist = playlist;
        this.mPlaylistCallback = callback;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public VideoDialogPresenter createPresenter() {
        Context context = this.mContext;
        return new VideoDialogPresenter(context, this, new VideoDataRepository(context));
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, AppConstant.Themes.THEMES_STYLE[new SettingPreferences(this.mContext).getThemes()]);

    }

    public Dialog onCreateDialog(Bundle bundle) {
        Dialog onCreateDialog = super.onCreateDialog(bundle);
        ((Window) Objects.requireNonNull(onCreateDialog.getWindow())).requestFeature(1);
        return onCreateDialog;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        Callback callback = this.mPlaylistCallback;
        if (callback != null) {
            callback.onDialogDismiss();
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_fragment_playlist_video, viewGroup, false);
        inflate.findViewById(R.id.iv_back).setOnClickListener(view -> {
            dismiss();
        });
        inflate.findViewById(R.id.iv_sort).setOnClickListener(view -> {
            BottomMenuDialogControl.getInstance().showSortDialogForVideo(this.mContext, new OkButtonClickListener() {
                public final void onClick(int i, boolean z) {
                    if (videoAdapter != null) {
                        videoAdapter.sortVideoList(i, z);
                    }
                }
            });
        });
        inflate.findViewById(R.id.tv_cancel_search).setOnClickListener(view -> {
            this.rlSearchView.setVisibility(4);
            this.rlTitle.setVisibility(0);
            VideoAdapter videoAdapter = this.videoAdapter;
            if (videoAdapter != null) {
                videoAdapter.updateVideoDataList(this.mVideos);
            }
        });
        ImageView imageView = (ImageView) inflate.findViewById(R.id.iv_done);
        this.ivDone = imageView;
        imageView.setOnClickListener(view -> {
            dismiss();
        });
        imageView = (ImageView) inflate.findViewById(R.id.iv_search);
        this.ivSearch = imageView;
        imageView.setOnClickListener(view -> {
            this.rlSearchView.setVisibility(0);
            this.rlTitle.setVisibility(4);
            this.searchView.setFocusable(true);
            this.searchView.setIconified(false);
            this.searchView.requestFocusFromTouch();
        });
        imageView = (ImageView) inflate.findViewById(R.id.iv_add_video);
        this.ivAddVideo = imageView;
        imageView.setOnClickListener(view -> {
            onAddVideoClick();
            Log.e("MusicPlaylistDialogFragment", "onClick: " );
        });
        ivViewMode = (ImageView) inflate.findViewById(R.id.iv_view_mode);
        ivViewMode.setOnClickListener(view -> {
            setViewMode();
        });

        this.loading = (ProgressBar) inflate.findViewById(R.id.loading);
        this.rlSearchView = (RelativeLayout) inflate.findViewById(R.id.rl_search_view);
        this.rlTitle = (RelativeLayout) inflate.findViewById(R.id.rl_title);
        this.rvVideoDialog = (RecyclerView) inflate.findViewById(R.id.rv_video_dialog);
        this.searchView = (SearchView) inflate.findViewById(R.id.search_view);
        this.tvFolderName = (TextView) inflate.findViewById(R.id.tv_folder_name);
        VideoAdapter videoAdapter = new VideoAdapter(requireActivity(), this.mIsSelectedMode, this, this.mPlaylistSelected);
        this.videoAdapter = videoAdapter;
        this.rvVideoDialog.setAdapter(videoAdapter);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.mContext, 2);
//        this.mGridLayoutManager = gridLayoutManager;

        if (viewMode == 1) {
            rvVideoDialog.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            rvVideoDialog.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }

//        this.rvVideoDialog.setLayoutManager(gridLayoutManager);
        int i = this.mType;
        if (i == 4) {
            this.tvFolderName.setText(R.string.recently_added);
            ((VideoDialogPresenter) this.mPresenter).getAllRecentlyVideo();
        } else if (i == 3) {
            this.tvFolderName.setText(R.string.favorite);
            ((VideoDialogPresenter) this.mPresenter).getAllFavoriteVideo();
        } else if (i == 1) {
            VideoFolder videoFolder = this.mVideoFolder;
            if (videoFolder != null) {
                this.tvFolderName.setText(videoFolder.getFolderName());
                ((VideoDialogPresenter) this.mPresenter).getAllVideoOfFolder(this.mVideoFolder);
            }
            if (this.mIsSelectedMode) {
                this.ivDone.setVisibility(0);
                this.ivSearch.setVisibility(4);
            }
        } else if (i == 2) {
            Playlist playlist = this.mPlaylist;
            if (playlist != null) {
                this.tvFolderName.setText(playlist.getPlaylistName());
                ((VideoDialogPresenter) this.mPresenter).getAllVideoOfPlaylist(this.mPlaylist);
            }
            this.ivAddVideo.setVisibility(0);
        } else {
            dismiss();
        }
        this.searchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                VideoPlaylistDialogFragment.this.searchView.clearFocus();
                return false;
            }

            public boolean onQueryTextChange(String str) {
                List searchVideoByVideoName = Utility.searchVideoByVideoName(VideoPlaylistDialogFragment.this.mVideos, str);
                if (VideoPlaylistDialogFragment.this.videoAdapter != null) {
                    VideoPlaylistDialogFragment.this.videoAdapter.updateVideoDataList(searchVideoByVideoName);
                }
                return false;
            }
        });
        if (this.mIsNewPlaylist) {
            onAddVideoClick();
        }
        return inflate;
    }
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public void onVideoLoader(List<VideoInfo> list) {
        Log.d("VideoPlaylist", "Loaded videos: " + list.size());
        this.mVideos = new ArrayList(list);
        this.videoAdapter.updateVideoDataList(list);

        if (list == null || list.isEmpty()) {
            loading.setVisibility(View.VISIBLE);
            rvVideoDialog.setVisibility(View.GONE);
        } else {
            loading.setVisibility(View.GONE);
            rvVideoDialog.setVisibility(View.VISIBLE);
        }


        this.loading.setVisibility(View.GONE);
        this.rvVideoDialog.setVisibility(View.VISIBLE);
    }

    private void setViewMode() {
        if (viewMode == 1) {
            viewMode = 2;
            ivViewMode.setImageResource(R.drawable.ic_grid_view);
            videoAdapter.setViewMode(2);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            rvVideoDialog.setLayoutManager(gridLayoutManager);
        } else {
            viewMode = 1;
            ivViewMode.setImageResource(R.drawable.ic_list_view);
            videoAdapter.setViewMode(1);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            rvVideoDialog.setLayoutManager(linearLayoutManager);
        }
    }
    public void onAddVideoClick() {
        new AddVideoPlaylistDialogFragment(this.mPlaylist, new AddVideoPlaylistDialogFragment.Callback() {
            @Override
            public void onDismiss() {
                // Re-fetch videos and update the adapter
                ((VideoDialogPresenter) mPresenter).getAllVideoOfPlaylist(mPlaylist);
            }
        }).show(getChildFragmentManager(), "dialog_playlist_add_video");
    }


    public void onMoreClick(int i, int i2, VideoInfo videoInfo) {
        boolean checkFavoriteVideoIdExisted = VideoFavoriteUtil.checkFavoriteVideoIdExisted(this.mContext, videoInfo.getId());
        final VideoInfo videoInfo2 = videoInfo;
        final int i3 = i2;
        final boolean z = checkFavoriteVideoIdExisted;
        final int i4 = i;
        BottomMenuDialogControl.getInstance().showMoreDialogVideo(this.mContext, checkFavoriteVideoIdExisted, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                VideoPlaylistDialogFragment.this.moreitemclick(videoInfo2, i3, z, i4, i);
            }
        });
    }

    public void renameVideo(String str, String str2, VideoInfo videoInfo, int i, String str3) {
        final String trim = str3.trim();
        if (TextUtils.isEmpty(str3)) {
            Toast.makeText(this.mContext, R.string.empty_video_name, 0).show();
        } else if (!trim.equals(str)) {
            if (VERSION.SDK_INT >= 30) {
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
                    IntentSenderRequest build = new Builder(MediaStore.createWriteRequest(contentResolver, Collections.singletonList(parse)).getIntentSender()).build();
                    this.positionVideoRequest = i;
                    this.videoRenameRequest = videoInfo;
                    this.launcherRenameVideo.launch(build);
                    return;
                }
            }
            final VideoInfo videoInfo2 = videoInfo;
            final String str4 = str2;
            final int i2 = i;
            Utility.renameAVideo(this.mContext, videoInfo, trim + str2, new OnScanCompletedListener() {
                public final void onScanCompleted(String str, Uri uri) {
                    VideoPlaylistDialogFragment.this.handlerequest(videoInfo2, trim, str4, i2, str, uri);
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

  

    public void moreitemclick(final VideoInfo videoInfo, int i, boolean z, final int i2, int i3) {
        String str = FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_MORE;
        switch (i3) {
            case 0:
                VideoFavoriteUtil.addFavoriteVideoId(this.mContext, videoInfo.getId(), z);
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
                        VideoPlaylistDialogFragment.this.renameVideo(str4, str3, videoInfo2, i4, str);
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
                        ContentResolver contentResolver = VideoPlaylistDialogFragment.this.mContext.getContentResolver();
                        PendingIntent pendingIntent = null;
                        try {
                            contentResolver.delete(parse, null, null);
                            VideoPlaylistDialogFragment.this.videoAdapter.removeItemPosition(i2);
                            VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
                            VideoFavoriteUtil.addFavoriteVideoId(VideoPlaylistDialogFragment.this.mContext, videoInfo.getId(), false);
                        } catch (SecurityException e) {
                            if (VERSION.SDK_INT >= 30) {
                                pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
                            } else if (VERSION.SDK_INT < 29) {
                                VideoPlaylistDialogFragment.this.videoAdapter.removeItemPosition(i2);
                                VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
                                VideoFavoriteUtil.addFavoriteVideoId(VideoPlaylistDialogFragment.this.mContext, videoInfo.getId(), false);
                            } else {
                                pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                            }
                            if (pendingIntent != null) {
                                IntentSenderRequest build = new Builder(pendingIntent.getIntentSender()).build();
                                VideoPlaylistDialogFragment.this.positionVideoRequest = i2;
                                VideoPlaylistDialogFragment.this.videoDeleteRequest = videoInfo;
                                VideoPlaylistDialogFragment.this.launcherDeleteVideo.launch(build);
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

    public void setIsNewPlaylist(boolean z) {
        this.mIsNewPlaylist = z;
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
                    if (VERSION.SDK_INT >= 30) {
                        contentResolver.update(parse, contentValues, null);
                        this.videoAdapter.notifyItemChanged(this.positionVideoRequest);
                    }
                } catch (SecurityException unused) {
                    Toast.makeText(this.mContext, R.string.an_error_occurred, 0).show();
                }
            }
        }
    }
}
