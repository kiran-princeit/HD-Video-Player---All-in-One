package hd.video.player.videoplayer.mplayer.masterplayer.fragments.music;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.activities.MusicPlaylistActivity;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.MusicInfoAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.MusicPlaylistAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.AdManager;
import hd.video.player.videoplayer.mplayer.masterplayer.base.BaseQuickAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.InputDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.InputDialogBuilder.OkButtonClickListener;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.music.MusicPlaylistPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicPlaylistView;

public class MusicPlaylistFragment extends BaseFragment<MusicPlaylistPresenter> implements MusicPlaylistView {
    private final ContentObserver playlistContentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean isChanged, Uri uri) {
            if (MusicPlaylistFragment.this.mPresenter != null) {
                ((MusicPlaylistPresenter) MusicPlaylistFragment.this.mPresenter).loadPlaylistTab();
            }
        }
    };
    private ImageView ivViewModeIcon;
    private ProgressBar loadingIndicator;
    private MusicPlaylistAdapter playlistAdapter;
    private Context fragmentContext;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView playlistRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvPlaylistCount;
    private int currentViewMode = 1;
    ImageView iv_empty;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.fragmentContext = context;
    }

    public MusicPlaylistPresenter createPresenter() {
        Context context = this.fragmentContext;
        return new MusicPlaylistPresenter(context, this, new MusicDataRepository(requireActivity()));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_music_playlist, container, false);
        this.playlistRecyclerView = rootView.findViewById(R.id.rv_content_tab);
        this.swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        this.loadingIndicator = rootView.findViewById(R.id.loading);
        this.tvPlaylistCount = rootView.findViewById(R.id.tv_total);
        this.ivViewModeIcon = rootView.findViewById(R.id.iv_view_mode);
        this.iv_empty = rootView.findViewById(R.id.iv_empty);

        ivViewModeIcon.setOnClickListener(view -> toggleViewMode());

        rootView.findViewById(R.id.iv_create_playlist).setOnClickListener(view -> createPlaylist());

        playlistAdapter = new MusicPlaylistAdapter(new ArrayList<>());
        playlistAdapter.setOnItemClickListener((adapter, view, position) ->
                AdManager.showInterstitial(getActivity(), () -> {
                    handlePlaylistItemClick(adapter, view, position);
                })
        );
        playlistAdapter.setOnItemChildClickListener((adapter, view, position) -> handlePlaylistItemChildClick(adapter, view, position));

        gridLayoutManager = new GridLayoutManager(this.fragmentContext, 1);
        this.playlistRecyclerView.setLayoutManager(gridLayoutManager);
        this.playlistRecyclerView.setAdapter(this.playlistAdapter);

        ItemAnimator itemAnimator = this.playlistRecyclerView.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> refreshPlaylist());

        return rootView;
    }

    public void toggleViewMode() {
        setViewMode();
    }

    public void handlePlaylistItemClick(BaseQuickAdapter adapter, View view, int position) {
        onPlaylistClick(position, (MusicPlaylist) playlistAdapter.getItem(position));
    }

    public void handlePlaylistItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        showPlaylistOptionsDialog(position);
    }

    public void showPlaylistOptionsDialog(int position) {
        BottomMenuDialogControl.getInstance().showMoreDialogPlaylist(fragmentContext, option -> handlePlaylistOptionSelection(option, position));
        FirebaseAnalyticsUtils.putEventClick(fragmentContext, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_PLAYLIST, "click_more_music_playlist");
    }

    public void handlePlaylistOptionSelection(int option, int position) {
        MusicPlaylist playlist = (MusicPlaylist) playlistAdapter.getItem(position);
        switch (option) {
            case 0:
                renamePlaylist(playlist, position);
                break;
            case 1:
                duplicatePlaylist(playlist, position);
                break;
            case 2:
                deletePlaylist(playlist, position);
                break;
        }
    }

    public void renamePlaylist(MusicPlaylist playlist, int position) {
        String playlistName = playlist.getPlaylistName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new InputDialogBuilder(fragmentContext, name -> updatePlaylistName(playlist, name, position), playlistName)
                    .setTitle(R.string.create_new_playlist, fragmentContext.getColor(R.color.app_color))
                    .build().show();
        }
        FirebaseAnalyticsUtils.putEventClick(fragmentContext, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_PLAYLIST_MORE, "click_item_rename_playlist");
    }

    public void duplicatePlaylist(MusicPlaylist playlist, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new InputDialogBuilder(fragmentContext, name -> handlePlaylistDuplication(playlist, name), "")
                    .setTitle(R.string.name_the_playlist, fragmentContext.getColor(R.color.app_color))
                    .build().show();
        }
        FirebaseAnalyticsUtils.putEventClick(fragmentContext, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_PLAYLIST_MORE, "click_item_duplicate_playlist");
    }

    public void deletePlaylist(MusicPlaylist playlist, int position) {

        new QuestionDialogBuilder(this.fragmentContext, new QuestionDialogBuilder.OkButtonClickListener() {
            public void onCancelClick() {
            }

            public void onOkClick() {
                if (MusicPlaylistFragment.this.playlistAdapter != null) {
                    MusicPlaylistFragment.this.playlistAdapter.remove(position);
                }
                ((MusicPlaylistPresenter) MusicPlaylistFragment.this.mPresenter).deletePlaylist(playlist);
            }
        }).setTitle(R.string.delete, this.fragmentContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_playlist).build().show();
        FirebaseAnalyticsUtils.putEventClick(this.fragmentContext, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_PLAYLIST_MORE, "click_item_delete_playlist");
    }

    public void updatePlaylistName(MusicPlaylist playlist, String newName, int position) {
        if (TextUtils.isEmpty(newName.trim())) {
            Toast.makeText(fragmentContext, R.string.empty_playlist_name, Toast.LENGTH_SHORT).show();
        } else {
            ((MusicPlaylistPresenter) mPresenter).updatePlaylistName(playlist, newName.trim(), position);
        }
    }

    public void handlePlaylistDuplication(MusicPlaylist playlist, String newName) {
        if (TextUtils.isEmpty(newName.trim())) {
            Toast.makeText(fragmentContext, R.string.empty_playlist_name, Toast.LENGTH_SHORT).show();
        } else {
            ((MusicPlaylistPresenter) mPresenter).duplicateMusicPlaylist(newName.trim(), playlist);
        }
    }

    public void setViewMode() {
        if (gridLayoutManager != null) {
            if (currentViewMode == 1) {
                gridLayoutManager.setSpanCount(3);
                ivViewModeIcon.setImageResource(R.drawable.ic_grid_view);
                currentViewMode = 2;
            } else {
                gridLayoutManager.setSpanCount(1);
                ivViewModeIcon.setImageResource(R.drawable.ic_list_view);
                currentViewMode = 1;
            }
            MusicPlaylist.viewMode = currentViewMode;
            if (playlistAdapter != null) {
                playlistAdapter.notifyDataSetChanged();
            }
        }
    }

    public void refreshPlaylist() {
        if (mPresenter != null) {
            ((MusicPlaylistPresenter) mPresenter).loadPlaylistTab();
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            ((MusicPlaylistPresenter) mPresenter).loadPlaylistTab();
        }
        if (playlistAdapter != null) {
            playlistAdapter.notifyItemChanged(0);
        }
        FirebaseAnalyticsUtils.putScreenChecking(fragmentContext, "Music_Playlist_Tab");
        requireActivity().getContentResolver().registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, playlistContentObserver);
    }

    public void onPause() {
        super.onPause();
        requireActivity().getContentResolver().unregisterContentObserver(playlistContentObserver);
    }

    public void updateMusicPlaylist(List<MusicPlaylist> playlists) {
        loadingIndicator.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        ArrayList<MusicPlaylist> updatedPlaylists = new ArrayList<>();
        updatedPlaylists.add(new MusicPlaylist()); // Default playlist
        updatedPlaylists.addAll(playlists);

        tvPlaylistCount.setText(getString(R.string.all_playlist, updatedPlaylists.size()));
        if (playlistAdapter != null) {
            playlistAdapter.setNewData(updatedPlaylists);
        }


        if (updatedPlaylists == null || updatedPlaylists.isEmpty()) {
            // Show the "No Music" message
            iv_empty.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);  // Hide the refresh layout
        } else {
            // Hide the "No Music" message and show the refresh layout
            iv_empty.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }

    }


    public void onCreatePlaylist(boolean isSuccess, MusicPlaylist playlist) {
        if (isSuccess) {
            MusicPlaylistDialogFragment playlistDialogFragment = new MusicPlaylistDialogFragment(1, playlist, new MusicPlaylistDialogFragment.Callback() {
                public void onDialogDismiss() {
                    loadPlaylistTab();
                }
            });
            playlistDialogFragment.setIsNewPlaylist(true);
            playlistDialogFragment.show(getChildFragmentManager().beginTransaction(), "dialog_playlist_music");
        } else {
            Toast.makeText(fragmentContext, R.string.duplicate_name, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDuplicationPlaylist(MusicPlaylist musicPlaylist) {
        if (musicPlaylist == null) {
            Toast.makeText(this.fragmentContext, R.string.duplicate_name, 0).show();
            return;
        }

        if (playlistAdapter != null) {
            playlistAdapter.addData((MusicPlaylist) musicPlaylist);
            this.playlistRecyclerView.smoothScrollToPosition(this.playlistAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onUpdatePlaylistName(int i, String str, boolean z) {
        if (z) {
            Toast.makeText(this.fragmentContext, R.string.successfully, 0).show();
            if (playlistAdapter != null) {
                MusicPlaylist musicPlaylist = (MusicPlaylist) playlistAdapter.getItem(i);
                if (musicPlaylist != null) {
                    musicPlaylist.setPlaylistName(str);
                    this.playlistAdapter.setData(i, musicPlaylist);
                }
            }
            return;
        }
        Toast.makeText(this.fragmentContext, R.string.duplicate_name, 0).show();
    }


    public void createPlaylist() {
        new InputDialogBuilder(fragmentContext, new OkButtonClickListener() {
            public final void onClick(String str) {
                if (TextUtils.isEmpty(str.trim())) {
                    Toast.makeText(MusicPlaylistFragment.this.fragmentContext, R.string.empty_playlist_name, 0).show();
                } else {
                    ((MusicPlaylistPresenter) MusicPlaylistFragment.this.mPresenter).createMusicPlaylist(str.trim());
                }
            }
        }, "").setTitle(R.string.create_new_playlist, getActivity().getResources().getColor(R.color.app_color)).build().show();
    }

    public void loadPlaylistTab() {
        if (mPresenter != null) {
            ((MusicPlaylistPresenter) mPresenter).loadPlaylistTab();
        }
    }


    public void onPlaylistClick(int position, MusicPlaylist playlist) {
        Intent intent = new Intent(fragmentContext, MusicPlaylistActivity.class);
        String eventType;

        if (position == 0) {
            intent.putExtra("type", 2); // Favorite
            eventType = "click_favorite";
        } else {
            intent.putExtra("type", 1); // Playlist
            intent.putExtra("music_playlist", playlist); // Pass the selected playlist
            eventType = "click_music_playlist";
        }

        FirebaseAnalyticsUtils.putEventClick(fragmentContext, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_PLAYLIST, eventType);
        fragmentContext.startActivity(intent);
    }


}
