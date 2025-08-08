package com.hdvideoplayer.smartplayer.player.fragments.video;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Video.Media;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.activities.VideoPlayListActivity;
import com.hdvideoplayer.smartplayer.player.adapters.video.PlaylistAdapter;
import com.hdvideoplayer.smartplayer.player.adsprosimple.AdManager;
import com.hdvideoplayer.smartplayer.player.customview.NpaGridLayoutManager;
import com.hdvideoplayer.smartplayer.player.data.entity.video.Playlist;
import com.hdvideoplayer.smartplayer.player.data.repository.VideoDataRepository;
import com.hdvideoplayer.smartplayer.player.dialog.BottomMenuDialogControl;
import com.hdvideoplayer.smartplayer.player.dialog.InputDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.QuestionDialogBuilder;
import com.hdvideoplayer.smartplayer.player.fragments.BaseFragment;

import com.hdvideoplayer.smartplayer.player.presenter.video.PlaylistPresenter;
import com.hdvideoplayer.smartplayer.player.util.FirebaseAnalyticsUtils;
import com.hdvideoplayer.smartplayer.player.util.Utility;
import com.hdvideoplayer.smartplayer.player.view.video.PlaylistView;

public class VideoPlaylistFragment extends BaseFragment<PlaylistPresenter> implements PlaylistView {
    private final ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (mPresenter != null) {
                mPresenter.openPlaylistTab();
            }
        }
    };

    private ProgressBar loading;
    private PlaylistAdapter mAdapter;
    private Context mContext;
    private NpaGridLayoutManager mGridLayoutManager;
    private RecyclerView mRvVideoTabContent;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvTotalPlaylist;
//    ImageView iv_empty;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public PlaylistPresenter createPresenter() {
        return new PlaylistPresenter(this, new VideoDataRepository(mContext));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_playlist, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        mRvVideoTabContent = view.findViewById(R.id.rv_content_tab);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        loading = view.findViewById(R.id.loading);
        tvTotalPlaylist = view.findViewById(R.id.tv_total);
//        iv_empty = view.findViewById(R.id.iv_empty);
        view.findViewById(R.id.iv_create_playlist).setOnClickListener(this::showCreatePlaylistDialog);
    }

    private void setupRecyclerView() {
        mAdapter = new PlaylistAdapter(new ArrayList<>());

        mAdapter.setOnItemClickListener((baseQuickAdapter, view, i) -> {
            AdManager.showInterstitial(getActivity(), () -> {
                onPlaylistClick(i, (Playlist
                        ) mAdapter.getItem(i));
            });

        });
        mAdapter.setOnItemChildClickListener((adapter, v, position) ->
                BottomMenuDialogControl.getInstance().showMoreDialogPlaylist(mContext, i ->
                        onPlaylistOptionSelect((Playlist) mAdapter.getItem(position), i, position)
                )
        );

        mGridLayoutManager = new NpaGridLayoutManager(mContext, 1);
        mRvVideoTabContent.setLayoutManager(mGridLayoutManager);
        mRvVideoTabContent.setAdapter(mAdapter);

        ItemAnimator itemAnimator = mRvVideoTabContent.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
    }

    private void setupListeners() {
        refreshLayout.setOnRefreshListener(() -> {
            if (mPresenter != null) {
                mPresenter.openPlaylistTab();
            }
        });
    }

    private void showCreatePlaylistDialog(View view) {
        new InputDialogBuilder(mContext, name -> {
            if (TextUtils.isEmpty(name.trim())) {
                Toast.makeText(mContext, R.string.empty_playlist_name, Toast.LENGTH_SHORT).show();
            } else {
                mPresenter.createNewPlaylist(name.trim());
            }
        }, "")
                .setTitle(R.string.create_new_playlist, getActivity().getResources().getColor(R.color.app_color))
                .build()
                .show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.openPlaylistTab();
        }
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(1);
        }
        FirebaseAnalyticsUtils.putScreenChecking(mContext, "Video_Playlist_Tab");
        requireActivity().getContentResolver().registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, contentObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().getContentResolver().unregisterContentObserver(contentObserver);
    }

    @Override
    public void updateVideoPlaylist(List<Playlist> playlists) {
        loading.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);

        ArrayList<Playlist> playlistData = new ArrayList<>();
        playlistData.add(new Playlist());
        playlistData.add(new Playlist());
        playlistData.addAll(playlists);

        tvTotalPlaylist.setText(getString(R.string.all_playlist, playlistData.size()));
        if (mAdapter != null) {
            mAdapter.setNewData(playlistData);
        }

//        if (playlists == null || playlists.isEmpty()) {
////            iv_empty.setVisibility(View.VISIBLE);
//            refreshLayout.setVisibility(View.GONE);
//        } else {
////            iv_empty.setVisibility(View.GONE);
//            refreshLayout.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onCreatePlaylist(boolean success, Playlist playlist) {
        if (success) {
            VideoPlaylistDialogFragment dialog = new VideoPlaylistDialogFragment(2, playlist, () -> {
                if (mPresenter != null) {
                    mPresenter.openPlaylistTab();
                }
            });
            dialog.setIsNewPlaylist(true);
            dialog.show(getChildFragmentManager().beginTransaction(), "dialog_playlist_video");
        } else {
            Toast.makeText(mContext, R.string.duplicate_name, Toast.LENGTH_SHORT).show();
        }
    }

    private void onPlaylistOptionSelect(Playlist playlist, int option, int position) {
        switch (option) {
            case 0:
                showRenamePlaylistDialog(playlist, position);
                break;
            case 1:
                showDuplicatePlaylistDialog(playlist);
                break;
            case 2:
                showDeletePlaylistDialog(playlist, position);
                break;
        }
    }

    private void showRenamePlaylistDialog(Playlist playlist, int position) {
        int color = Utility.getColorAttr(mContext, Utility.getColorAttr(mContext, R.color.app_color), R.color.color_787D85);
        new InputDialogBuilder(mContext, name -> {
            if (TextUtils.isEmpty(name.trim())) {
                Toast.makeText(mContext, R.string.empty_playlist_name, Toast.LENGTH_SHORT).show();
            } else if (!name.trim().equals(playlist.getPlaylistName())) {
                mPresenter.updatePlaylistName(playlist, name.trim(), position);
            }
        }, playlist.getPlaylistName())
                .setTitle(R.string.create_new_playlist, color)
                .build()
                .show();
    }

    private void showDuplicatePlaylistDialog(Playlist playlist) {
        int color = Utility.getColorAttr(mContext, Utility.getColorAttr(mContext, R.color.app_color), R.color.color_787D85);
        new InputDialogBuilder(mContext, name -> {
            if (TextUtils.isEmpty(name.trim())) {
                Toast.makeText(mContext, R.string.empty_playlist_name, Toast.LENGTH_SHORT).show();
            } else {
                mPresenter.duplicateVideoPlaylist(name.trim(), playlist);
            }
        }, "")
                .setTitle(R.string.name_the_playlist, color)
                .build()
                .show();
    }

    private void showDeletePlaylistDialog(Playlist playlist, int position) {
        new QuestionDialogBuilder(mContext, new QuestionDialogBuilder.OkButtonClickListener() {
            @Override
            public void onOkClick() {
                if (mAdapter != null) {
                    mAdapter.remove(position);
                }
                mPresenter.deletePlaylist(playlist);
            }

            @Override
            public void onCancelClick() {
            }
        })
                .setTitle(R.string.delete, mContext.getResources().getColor(R.color.color_FF6666))
                .setQuestion(R.string.question_remove_playlist)
                .build()
                .show();
    }


    private void onPlaylistClick(int position, Playlist playlist) {
        Intent intent = new Intent(getContext(), VideoPlayListActivity.class);

        if (position == 0) {
            // For recently watched videos
            intent.putExtra("folder_type", 4);
        } else if (position == 1) {
            // For favorite videos
            intent.putExtra("folder_type", 3);
        } else {
            // For a specific playlist
            intent.putExtra("folder_type", 2);
            if (mPresenter != null) {
                mPresenter.openPlaylistTab();
            }// Type for "playlist_video"
            intent.putExtra("playlist", playlist);
        }

        startActivity(intent);
    }

    @Override
    public void onUpdatePlaylistName(int position, String name, boolean success) {
        if (success) {
            Toast.makeText(mContext, R.string.successfully, Toast.LENGTH_SHORT).show();
            Playlist playlist = (Playlist) mAdapter.getItem(position);
            if (playlist != null) {
                playlist.setPlaylistName(name);
                mAdapter.setData(position, playlist);
            }
        } else {
            Toast.makeText(mContext, R.string.duplicate_name, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDuplicationPlaylist(Playlist playlist) {
        if (playlist == null) {
            Toast.makeText(mContext, R.string.duplicate_name, Toast.LENGTH_SHORT).show();
        } else {
            mAdapter.addData(playlist);
            mRvVideoTabContent.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    }
}



