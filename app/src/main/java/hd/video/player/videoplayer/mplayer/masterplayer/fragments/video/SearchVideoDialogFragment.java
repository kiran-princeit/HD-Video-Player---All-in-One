package hd.video.player.videoplayer.mplayer.masterplayer.fragments.video;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.IntentSenderRequest.Builder;
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.video.VideoAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.VideoDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.VideoDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.SettingPreferences;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.VideoFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.AddToPlaylistDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.InputDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MediaInfoDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseDialogFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.video.VideoSearchingPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.view.video.VideoSearchView;
import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant;

public class SearchVideoDialogFragment extends BaseDialogFragment<VideoSearchingPresenter> implements VideoSearchView, VideoAdapter.Callback {
    private final ActivityResultLauncher<IntentSenderRequest> launcherDeleteVideo = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            SearchVideoDialogFragment.this.handlelauncherDeleteMusic((ActivityResult) obj);
        }
    });
    private final ActivityResultLauncher<IntentSenderRequest> launcherRenameVideo = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            SearchVideoDialogFragment.this.handlelauncherRenameMusic((ActivityResult) obj);
        }
    });
    private VideoAdapter videoAdapter;
    private Context mContext;
    private int positionVideoRequest = -1;
    RecyclerView rvSearchResult;
    SearchView searchView;
    private VideoInfo videoDeleteRequest = null;
    private VideoInfo videoRenameRequest = null;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public VideoSearchingPresenter createPresenter() {
        return new VideoSearchingPresenter(this.mContext, this, new VideoDataRepository(this.mContext));
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

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_fragment_search, viewGroup, false);
        inflate.findViewById(R.id.tv_cancel_search).setOnClickListener(view -> {
            dismiss();
        });
        this.rvSearchResult = (RecyclerView) inflate.findViewById(R.id.rv_search_result);
        this.searchView = (SearchView) inflate.findViewById(R.id.search_view);
        VideoAdapter videoAdapter = new VideoAdapter(requireActivity(), false, this, null);
        this.videoAdapter = videoAdapter;
        this.rvSearchResult.setAdapter(videoAdapter);
        this.rvSearchResult.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.searchView.setFocusable(true);
        this.searchView.setIconified(false);
        this.searchView.requestFocusFromTouch();
        this.searchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                SearchVideoDialogFragment.this.searchView.clearFocus();
                return false;
            }

            public boolean onQueryTextChange(String str) {
                if (SearchVideoDialogFragment.this.mPresenter != null) {
                    ((VideoSearchingPresenter) SearchVideoDialogFragment.this.mPresenter).searchVideoByVideoName(str);
                }
                return false;
            }
        });
        if (this.mPresenter != null) {
            ((VideoSearchingPresenter) this.mPresenter).searchVideoByVideoName("");
        }
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
       
    }

    public void onSearchVideo(List<VideoInfo> list) {
        VideoAdapter videoAdapter = this.videoAdapter;
        if (videoAdapter != null) {
            videoAdapter.updateVideoDataList(list);
        }
    }

    public void onMoreClick(int i, int i2, VideoInfo videoInfo) {
        boolean checkFavoriteVideoIdExisted = VideoFavoriteUtil.checkFavoriteVideoIdExisted(this.mContext, videoInfo.getId());
        final VideoInfo videoInfo2 = videoInfo;
        final int i3 = i2;
        final boolean z = checkFavoriteVideoIdExisted;
        final int i4 = i;
        BottomMenuDialogControl.getInstance().showMoreDialogVideo(this.mContext, checkFavoriteVideoIdExisted, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                SearchVideoDialogFragment.this.m710x4469291e(videoInfo2, i3, z, i4, i);
            }
        });
    }

    public void m709x5ebdcc9d(String str, String str2, VideoInfo videoInfo, int i, String str3) {
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
                    SearchVideoDialogFragment.this.m708x7912701c(videoInfo2, trim, str4, i2, str, uri);
                }
            });
        }
    }

    public void m708x7912701c(VideoInfo videoInfo, String str, String str2, final int i, String str3, Uri uri) {
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
    
    public void m710x4469291e(final VideoInfo videoInfo, int i, boolean z, final int i2, int i3) {
        String str = FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_MORE;
        switch (i3) {
            case 0:
                VideoFavoriteUtil.addFavoriteVideoId(this.mContext, videoInfo.getId(), z);
                FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_favorite");
                return;
            case 1:
                List allPlaylist = MyDatabase.getInstance(this.mContext).videoPlaylistDAO().getAllPlaylist();
                if (allPlaylist == null || allPlaylist.isEmpty()) {
                    Toast.makeText(this.mContext, "you haven't created any playlist yet", 0).show();
                    return;
                } else {
                    new AddToPlaylistDialogBuilder(this.mContext, videoInfo.getId(), allPlaylist, null).build().show();
                    return;
                }
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
                        SearchVideoDialogFragment.this.m709x5ebdcc9d(str4, str3, videoInfo2, i4, str);
                    }
                }, displayName).setTitle(R.string.rename,colors).build().show();
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
                        ContentResolver contentResolver = SearchVideoDialogFragment.this.mContext.getContentResolver();
                        PendingIntent pendingIntent = null;
                        try {
                            contentResolver.delete(parse, null, null);
                            SearchVideoDialogFragment.this.videoAdapter.removeItemPosition(i2);
                            VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
                            VideoFavoriteUtil.addFavoriteVideoId(SearchVideoDialogFragment.this.mContext, videoInfo.getId(), false);
                        } catch (SecurityException e) {
                            if (VERSION.SDK_INT >= 30) {
                                pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
                            } else if (VERSION.SDK_INT < 29) {
                                SearchVideoDialogFragment.this.videoAdapter.removeItemPosition(i2);
                                VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
                                VideoFavoriteUtil.addFavoriteVideoId(SearchVideoDialogFragment.this.mContext, videoInfo.getId(), false);
                            } else {
                                pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                            }
                            if (pendingIntent != null) {
                                IntentSenderRequest build = new Builder(pendingIntent.getIntentSender()).build();
                                SearchVideoDialogFragment.this.positionVideoRequest = i2;
                                SearchVideoDialogFragment.this.videoDeleteRequest = videoInfo;
                                SearchVideoDialogFragment.this.launcherDeleteVideo.launch(build);
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
