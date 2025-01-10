package hd.video.player.videoplayer.mplayer.masterplayer.fragments.video;

import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.IntentSenderRequest.Builder;
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Collections;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.video.VideoInfoAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.VideoDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.VideoDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.VideoFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.AddToPlaylistDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.InputDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MediaInfoDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.video.VideoInfoPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.view.video.VideoInfoView;

public class VideoAllFragment extends BaseFragment<VideoInfoPresenter> implements VideoInfoView, VideoInfoAdapter.Callback {

    private final ContentObserver contentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, Uri uri) {
            if (mPresenter != null) {
                mPresenter.openVideosTab();
            }
        }
    };

    private ImageView ivViewMode;
    private ProgressBar loading;
    private VideoInfoAdapter mAdapter;
    private Context mContext;
    private RecyclerView recyclerView;
    private int viewMode = 1;
    private int positionVideoRequest = -1;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvTotalVideo;
    private VideoInfo videoDeleteRequest = null;
    private VideoInfo videoRenameRequest = null;
    private final ActivityResultLauncher<IntentSenderRequest> launcherDeleteVideo =
            registerForActivityResult(new StartIntentSenderForResult(), result -> deleteRequest(result));

    private final ActivityResultLauncher<IntentSenderRequest> launcherRenameVideo =
            registerForActivityResult(new StartIntentSenderForResult(), result -> renameRequest(result));

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public VideoInfoPresenter createPresenter() {
        return new VideoInfoPresenter(mContext, this, new VideoDataRepository(mContext));
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = layoutInflater.inflate(R.layout.fragment_video_all, container, false);
        recyclerView = inflate.findViewById(R.id.rv_content_tab);
        loading = inflate.findViewById(R.id.loading);
        refreshLayout = inflate.findViewById(R.id.swipe_refresh);
        ivViewMode = inflate.findViewById(R.id.iv_view_mode);
        tvTotalVideo = inflate.findViewById(R.id.tv_total_video);

        ivViewMode.setImageResource(R.drawable.ic_grid_view);

        ivViewMode.setOnClickListener(view -> setViewMode());
        inflate.findViewById(R.id.iv_sort).setOnClickListener(view -> sortVideoList());

        mAdapter = new VideoInfoAdapter(getActivity(), false, this, null);

        // Setting layout manager based on viewMode
        recyclerView.setLayoutManager(viewMode == 1 ? new LinearLayoutManager(getActivity()) : new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(mAdapter);

        ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }

        refreshLayout.setOnRefreshListener(() -> {
            if (mPresenter != null) {
                mPresenter.openVideosTab();
            }
        });

        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.openVideosTab();
        }
        requireActivity().getContentResolver().registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, contentObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().getContentResolver().unregisterContentObserver(contentObserver);
    }

    @Override
    public void updateVideoList(List<VideoInfo> list) {
        loading.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        tvTotalVideo.setText(getString(R.string.all_video, list.size()));
        if (mAdapter != null) {
            mAdapter.updateVideoDataList(list);
        }
    }


    public void deleteRequest(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionVideoRequest;
            if (i >= 0 && i < this.mAdapter.getItemCount()) {
                this.mAdapter.removeItemPosition(this.positionVideoRequest);
            }
            if (this.videoDeleteRequest != null) {
                VideoDatabaseControl.getInstance().removeVideoById(this.videoDeleteRequest.getId());
                VideoFavoriteUtil.addFavoriteVideoId(this.mContext, this.videoDeleteRequest.getId(), false);
            }
        }
    }

    public void renameRequest(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionVideoRequest;
            if (i >= 0 && i < this.mAdapter.getItemCount() && this.videoRenameRequest != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", this.videoRenameRequest.getDisplayName());
                Uri parse = Uri.parse(this.videoRenameRequest.getUri());
                ContentResolver contentResolver = this.mContext.getContentResolver();
                try {
                    if (VERSION.SDK_INT >= 30) {
                        contentResolver.update(parse, contentValues, null);
                        this.mAdapter.notifyItemChanged(this.positionVideoRequest);
                    }
                } catch (SecurityException unused) {
                    Toast.makeText(this.mContext, R.string.an_error_occurred, 0).show();
                }
            }
        }
    }

    private void setViewMode() {
        if (viewMode == 1) {
            viewMode = 2;
            ivViewMode.setImageResource(R.drawable.ic_grid_view);
            mAdapter.setViewMode(2);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            viewMode = 1;
            ivViewMode.setImageResource(R.drawable.ic_list_view);
            mAdapter.setViewMode(1);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    public void sortVideoList() {
        BottomMenuDialogControl.getInstance().showSortDialogForVideo(mContext, (i, z) -> sortList(i, z));
    }

    private void sortList(int i, boolean z) {
        mAdapter.sortVideoList(i, z);
    }

    @Override
    public void onMoreClick(int position, int adapterPosition, VideoInfo videoInfo) {
        boolean isFavorite = VideoFavoriteUtil.checkFavoriteVideoIdExisted(mContext, videoInfo.getId());
        BottomMenuDialogControl.getInstance().showMoreDialogVideo(mContext, isFavorite, (option) -> menuMore(videoInfo, adapterPosition, isFavorite, position, option));
    }

    public void getVideoInfo(String originalName, String suffix, VideoInfo videoInfo, int position, String newName) {
        String trimmedName = newName.trim();
        if (TextUtils.isEmpty(trimmedName)) {
            Toast.makeText(mContext, R.string.empty_video_name, Toast.LENGTH_SHORT).show();
        } else if (!trimmedName.equals(originalName)) {
            if (Build.VERSION.SDK_INT >= 30) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", trimmedName + suffix);
                Uri videoUri = Uri.parse(videoInfo.getUri());
                videoInfo.setDisplayName(trimmedName + suffix);
                try {
                    mContext.getContentResolver().update(videoUri, contentValues, null);
                    mAdapter.notifyItemChanged(position);
                } catch (SecurityException e) {
                    IntentSenderRequest request = new IntentSenderRequest.Builder(MediaStore.createWriteRequest(mContext.getContentResolver(), Collections.singletonList(videoUri)).getIntentSender()).build();
                    launcherRenameVideo.launch(request);
                }
            } else {
                Utility.renameAVideo(mContext, videoInfo, trimmedName + suffix, (path, uri) -> renameVideo(videoInfo, trimmedName, suffix, position, path, uri));
            }
        }
    }

    private void renameVideo(VideoInfo videoInfo, String newName, String suffix, int position, String path, Uri uri) {
        if (uri != null) {
            videoInfo.setDisplayName(newName + suffix);
            videoInfo.setPath(path);
            videoInfo.setUri(uri.toString());
            long parseId = 0;
            try {
                parseId = ContentUris.parseId(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
            videoInfo.setId(parseId);
            VideoDatabaseControl.getInstance().addVideo(videoInfo);
            new Handler(Looper.getMainLooper()).post(() -> mAdapter.notifyItemChanged(position));
        }
    }


    public void menuMore(final VideoInfo videoInfo, int i, boolean z, final int i2, int i3) {
        switch (i3) {
            case 0:
                VideoFavoriteUtil.addFavoriteVideoId(this.mContext, videoInfo.getId(), !z);

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
                String str = ".";
                if (displayName.indexOf(str) > 0) {
                    substring = displayName.substring(displayName.lastIndexOf(str));
                    displayName = displayName.substring(0, displayName.lastIndexOf(str));
                } else {
                    substring = "";
                }
                String finalDisplayName = displayName;

                int color = Utility.getColorAttr(mContext, Utility.getColorAttr(this.mContext, R.color.app_color), R.color.color_787D85);

                new InputDialogBuilder(this.mContext, new InputDialogBuilder.OkButtonClickListener() {
                    @Override
                    public void onClick(String str) {
                        getVideoInfo(finalDisplayName, substring, videoInfo, i2, str);
                    }
                }, displayName)
                        .setTitle(R.string.rename, color)
                        .build()
                        .show();

                return;
            case 3:
                Utility.shareVideo(this.mContext, videoInfo);
                return;
            case 4:
                new MediaInfoDialogBuilder(this.mContext, videoInfo).build().show();
                return;
            case 5:
                new QuestionDialogBuilder(this.mContext, new QuestionDialogBuilder.OkButtonClickListener() {
                    public void onCancelClick() {
                    }

                    public void onOkClick() {
                        Uri parse = Uri.parse(videoInfo.getUri());
                        ContentResolver contentResolver = VideoAllFragment.this.mContext.getContentResolver();
                        PendingIntent pendingIntent = null;
                        try {
                            contentResolver.delete(parse, null, null);
                            VideoAllFragment.this.mAdapter.removeItemPosition(i2);
                            VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
                            VideoFavoriteUtil.addFavoriteVideoId(VideoAllFragment.this.mContext, videoInfo.getId(), false);
                        } catch (SecurityException e) {
                            if (VERSION.SDK_INT >= 30) {
                                pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
                            } else if (VERSION.SDK_INT < 29) {
                                VideoAllFragment.this.mAdapter.removeItemPosition(i2);
                                VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
                                VideoFavoriteUtil.addFavoriteVideoId(VideoAllFragment.this.mContext, videoInfo.getId(), false);
                            } else {
                                pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                            }
                            if (pendingIntent != null) {
                                IntentSenderRequest build = new Builder(pendingIntent.getIntentSender()).build();
                                VideoAllFragment.this.positionVideoRequest = i2;
                                VideoAllFragment.this.videoDeleteRequest = videoInfo;
                                VideoAllFragment.this.launcherDeleteVideo.launch(build);
                            }
                        }
                    }
                }).setTitle(R.string.delete, this.mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
                return;
            default:
                return;
        }
    }
}
