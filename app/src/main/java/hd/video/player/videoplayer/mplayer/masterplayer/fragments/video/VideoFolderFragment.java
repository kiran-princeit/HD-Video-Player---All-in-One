package hd.video.player.videoplayer.mplayer.masterplayer.fragments.video;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Video.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import hd.video.player.videoplayer.mplayer.masterplayer.activities.VideoPlayListActivity;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.video.VideoFoldersAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.AdManager;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.VideoDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoFolder;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.VideoDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.FolderInfoDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.video.VideoFolderPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.view.video.VideoFolderView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.VideoFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;

public class VideoFolderFragment extends BaseFragment<VideoFolderPresenter> implements VideoFolderView, VideoFoldersAdapter.Callback {
    private final ContentObserver contentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean z, Uri uri) {
            if (VideoFolderFragment.this.mPresenter != null) {
                ((VideoFolderPresenter) VideoFolderFragment.this.mPresenter).openFoldersTab();
            }
        }
    };
    private ImageView ivViewMode;
    private ProgressBar loading;
    private VideoFoldersAdapter mAdapter;
    private Context mContext;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvTotalFolder;
    private int viewMode = 1;
    RecyclerView recyclerView;
    ImageView iv_empty;

    public class AnonymousClass4 implements QuestionDialogBuilder.OkButtonClickListener {
        final int val$position;
        final VideoFolder val$videoFolder;

        public void onCancelClick() {
        }

        AnonymousClass4(VideoFolder videoFolder, int i) {
            this.val$videoFolder = videoFolder;
            this.val$position = i;
        }

        public void onOkClick() {
            final Dialog dialog = new Dialog(VideoFolderFragment.this.mContext);
            dialog.getWindow().requestFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_loading);
            dialog.setCancelable(false);
            dialog.show();
            final VideoFolder videoFolder = this.val$videoFolder;
            Single.defer(new Callable() {
                public final Object call() throws Exception {
                    return AnonymousClass4.this.m729x3c55f178(videoFolder);
                }
            }).delay(200, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Boolean>() {
                public void onSubscribe(Disposable disposable) {
                }

                public void onSuccess(Boolean bool) {
                    if (VideoFolderFragment.this.mAdapter != null) {
                        VideoFolderFragment.this.mAdapter.removeItemPosition(AnonymousClass4.this.val$position);
                        VideoFolderFragment.this.mAdapter.updateRecently();
                    }
                    dialog.dismiss();
                }

                public void onError(Throwable th) {
                    dialog.dismiss();
                }
            });
        }

        public SingleSource m729x3c55f178(VideoFolder videoFolder) throws Exception {
            return Single.just(Boolean.valueOf(VideoFolderFragment.this.deleteFolder(videoFolder)));
        }
    }


    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public VideoFolderPresenter createPresenter() {
        return new VideoFolderPresenter(this, new VideoDataRepository(this.mContext));
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_video_folder_all, viewGroup, false);
        if (this.mPresenter != null) {
            ((VideoFolderPresenter) this.mPresenter).openFoldersTab();
        }
        recyclerView = (RecyclerView) inflate.findViewById(R.id.rv_content_tab);
        this.refreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh);
        this.loading = (ProgressBar) inflate.findViewById(R.id.loading);
        this.tvTotalFolder = (TextView) inflate.findViewById(R.id.tv_total);
        this.ivViewMode = (ImageView) inflate.findViewById(R.id.iv_view_mode);
        iv_empty = inflate.findViewById(R.id.iv_empty);

        ivViewMode.setOnClickListener(view -> {
            setViewMode();
        });
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.mContext, 2);
//        this.mGridLayoutManager = gridLayoutManager;
//        recyclerView.setLayoutManager(gridLayoutManager);

        if (viewMode == 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        VideoFoldersAdapter videoFoldersAdapter = new VideoFoldersAdapter(this.mContext, false, this, getActivity());
        this.mAdapter = videoFoldersAdapter;
        recyclerView.setAdapter(videoFoldersAdapter);
        ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        this.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public final void onRefresh() {
                if (mPresenter != null) {
                    ((VideoFolderPresenter) mPresenter).openFoldersTab();
                }
            }
        });
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

    }

    public void onResume() {
        super.onResume();
        if (this.mPresenter != null) {
            ((VideoFolderPresenter) this.mPresenter).openFoldersTab();
        }
        FirebaseAnalyticsUtils.putScreenChecking(this.mContext, "Video_Folder_Tab");
        requireActivity().getContentResolver().registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, this.contentObserver);
    }

    public void onPause() {
        super.onPause();
        requireActivity().getContentResolver().unregisterContentObserver(this.contentObserver);
    }

    private void setViewMode() {
        if (viewMode == 1) {
            viewMode = 2;
            ivViewMode.setImageResource(R.drawable.ic_grid_view);
            mAdapter.setViewMode(2);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
        } else {
            viewMode = 1;
            ivViewMode.setImageResource(R.drawable.ic_list_view);
            mAdapter.setViewMode(1);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }

    public void updateFolderList(List<VideoFolder> list) {
        this.loading.setVisibility(8);
        this.refreshLayout.setVisibility(0);
        this.refreshLayout.setRefreshing(false);
        this.tvTotalFolder.setText(getString(R.string.all_folder, Integer.valueOf(list.size() + 1)));
        VideoFoldersAdapter videoFoldersAdapter = this.mAdapter;
        if (videoFoldersAdapter != null) {
            videoFoldersAdapter.updateVideoFolders(list);
        }
        if (list == null || list.isEmpty()) {
            iv_empty.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }

    }

    public boolean deleteFolder(VideoFolder videoFolder) {
        for (VideoInfo videoInfo : videoFolder.getVideoList()) {
            VideoDatabaseControl.getInstance().removeVideoById(videoInfo.getId());
            VideoFavoriteUtil.addFavoriteVideoId(this.mContext, videoInfo.getId(), false);
            Utility.deleteAVideo(this.mContext, videoInfo);
        }
        return true;
    }

    public void onFolderOptionSelect(VideoFolder videoFolder, int i) {
        if (videoFolder != null) {
            BottomMenuDialogControl.getInstance().showMoreDialogVideoFolder(this.mContext, new BottomMenuAdapter.Callback() {
                @Override
                public void onClick(int i) {
                    String str = FirebaseAnalyticsUtils.EVENT_PROX_VIDEO_FOLDER_MORE;
                    if (i == 0) {
                        new QuestionDialogBuilder(mContext, new AnonymousClass4(videoFolder, i)).setTitle(R.string.delete, mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
                        FirebaseAnalyticsUtils.putEventClick(mContext, str, "click_delete_folder");
                    } else if (i == 1) {
                        new FolderInfoDialogBuilder(mContext, videoFolder).build().show();
                        FirebaseAnalyticsUtils.putEventClick(mContext, str, "click_info_folder");
                    }
                }
            });
        }
    }
    public void onFolderClick(VideoFolder videoFolder, int i) {
        AdManager.showInterstitial(getActivity(), () -> {
            Intent intent = new Intent(getActivity(), VideoPlayListActivity.class);

            if (i == 0) {
                intent.putExtra("folder_type", 4);  // Pass type for recently added videos
            } else if (videoFolder != null) {
                intent.putExtra("folder_type", 1);  // Pass type for specific folder videos
                intent.putExtra("video_folder", videoFolder);

            }

            startActivity(intent);
        });
    }


}
