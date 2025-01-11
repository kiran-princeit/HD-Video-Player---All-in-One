package hd.video.player.videoplayer.mplayer.masterplayer.fragments.music;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.MusicHistoryAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.MusicHistoryAdapter.Callback;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.MusicDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicArtist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicHistory;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.MusicFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MediaInfoDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder.OkButtonClickListener;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.music.MusicHistoryPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.util.thread.ThreadExecutor;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicHistoryView;


public class MusicHistoryFragment extends BaseFragment<MusicHistoryPresenter> implements MusicHistoryView, Callback {
    private ProgressBar loading;
    private MusicHistoryAdapter mAdapter;
    private Context mContext;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvTotal;
    ImageView iv_empty;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public MusicHistoryPresenter createPresenter() {
        return new MusicHistoryPresenter(mContext, this, new MusicDataRepository(requireActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_main, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_content_tab);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        tvTotal = view.findViewById(R.id.tv_total);
        loading = view.findViewById(R.id.loading);
        iv_empty = view.findViewById(R.id.iv_empty);

        // Hiding unnecessary icons
        view.findViewById(R.id.iv_sort).setVisibility(View.GONE);
        ImageView deleteIcon = view.findViewById(R.id.iv_delete);
        deleteIcon.setVisibility(View.VISIBLE);
        deleteIcon.setOnClickListener(v -> onDeleteClick());

        // Set up RecyclerView
        mAdapter = new MusicHistoryAdapter(requireActivity(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(mAdapter);

        // Disable change animations in RecyclerView
        ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }

        // Set up SwipeRefreshLayout listener
        refreshLayout.setOnRefreshListener(() -> {
            if (mPresenter != null) {
                ((MusicHistoryPresenter) mPresenter).openMusicHistoryTab();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            ((MusicHistoryPresenter) mPresenter).openMusicHistoryTab();
        }
        FirebaseAnalyticsUtils.putScreenChecking(mContext, "Music_History_Tab");
    }

    // Action when delete icon is clicked
    public void onDeleteClick() {
        new QuestionDialogBuilder(mContext, new OkButtonClickListener() {
            @Override
            public void onCancelClick() {
                // No action
            }

            @Override
            public void onOkClick() {
                if (mPresenter != null) {
                    ((MusicHistoryPresenter) mPresenter).deleteAllMusicHistory();
                }
                if (mAdapter != null) {
                    mAdapter.updateMusicHistoryList(new ArrayList<>());
                }
            }
        })
                .setTitle(R.string.delete_all, mContext.getResources().getColor(R.color.color_FF6666))
                .setQuestion(R.string.question_remove_all_history_video)
                .build()
                .show();
    }

    // Update the list of music history
    @Override
    public void updateMusicHistoryList(List<MusicHistory> list) {
        loading.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        tvTotal.setText(getString(R.string.all_history, list.size()));
        if (mAdapter != null) {
            mAdapter.updateMusicHistoryList(list);
        }
        if (list == null || list.isEmpty()) {
            iv_empty.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }




    @Override
    public void onMusicOptionSelected(MusicHistory musicHistory, int position, int optionId) {
        String event = FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_HISTORY_MORE;
        switch (optionId) {
            case 0:
                new QuestionDialogBuilder(mContext, new OkButtonClickListener() {
                    @Override
                    public void onCancelClick() {
                        // No action
                    }

                    @Override
                    public void onOkClick() {
                        if (mPresenter != null) {
                            ((MusicHistoryPresenter) mPresenter).deleteHistoryMusicById(musicHistory.getId());
                        }
                        if (mAdapter != null) {
                            mAdapter.removeItemAtPosition(position);
                        }
                    }
                })
                        .setTitle(R.string.delete, mContext.getResources().getColor(R.color.color_FF6666))
                        .setQuestion(R.string.question_remove_a_history_video)
                        .build()
                        .show();
                FirebaseAnalyticsUtils.putEventClick(mContext, event, "click_remove_from_history");
                break;

            case 1:
                new QuestionDialogBuilder(mContext, new MusicHistoryDeleteListener(position, musicHistory))
                        .setTitle(R.string.delete, mContext.getResources().getColor(R.color.color_FF6666))
                        .setQuestion(R.string.question_remove_file)
                        .build()
                        .show();
                FirebaseAnalyticsUtils.putEventClick(mContext, event, "click_delete_item");
                break;

            case 2:
                new MediaInfoDialogBuilder(mContext, musicHistory.getMusics()).build().show();
                FirebaseAnalyticsUtils.putEventClick(mContext, event, "click_item_info");
                break;
        }
    }

    private class MusicHistoryDeleteListener implements OkButtonClickListener {
        final MusicHistory musicHistory;
        final int position;

        MusicHistoryDeleteListener(int position, MusicHistory musicHistory) {
            this.position = position;
            this.musicHistory = musicHistory;
        }

        @Override
        public void onCancelClick() {
            // No action
        }

        @Override
        public void onOkClick() {
            if (mAdapter != null) {
                mAdapter.removeItemAtPosition(position);
            }
            if (mPresenter != null) {
                ((MusicHistoryPresenter) mPresenter).deleteHistoryMusicById(musicHistory.getId());
            }
            deleteMusicFromDatabase(musicHistory);
        }

        private void deleteMusicFromDatabase(final MusicHistory musicHistory) {
            ThreadExecutor.runOnDatabaseThread(() -> {
                MusicDatabaseControl.getInstance().removeMusicById(musicHistory.getId());
                MusicFavoriteUtil.addFavoriteMusicId(mContext, musicHistory.getId(), false);
                Utility.deleteMusicFiles(mContext, musicHistory.getMusics());
            });
        }
    }

}