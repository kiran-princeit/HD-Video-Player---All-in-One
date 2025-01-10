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



    // Renamed anonymous inner class to something more meaningful
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
    }



    // Handle music option selection
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

}


//public class MusicHistoryTabFragment extends BaseFragment<MusicHistoryPresenter> implements MusicHistoryView, Callback {
//    private ProgressBar loading;
//    private MusicHistoryAdapter mAdapter;
//    private Context mContext;
//    private SwipeRefreshLayout refreshLayout;
//    private TextView tvTotal;
//
//    public class AnonymousClass5 implements OkButtonClickListener {
//        final MusicHistory val$musicHistory;
//        final int val$position;
//
//        public void onCancelClick() {
//        }
//
//        AnonymousClass5(int i, MusicHistory musicHistory) {
//            this.val$position = i;
//            this.val$musicHistory = musicHistory;
//        }
//
//        public void onOkClick() {
//            if (MusicHistoryTabFragment.this.mAdapter != null) {
//                MusicHistoryTabFragment.this.mAdapter.removeItemPosition(this.val$position);
//            }
//            if (MusicHistoryTabFragment.this.mPresenter != null) {
//                ((MusicHistoryPresenter) MusicHistoryTabFragment.this.mPresenter).deleteAHistoryMusicById(this.val$musicHistory.getId());
//            }
//            final MusicHistory musicHistory = this.val$musicHistory;
//            ThreadExecutor.runOnDatabaseThread(new Runnable() {
//                public final void run() {
//                    AnonymousClass5.this.m681x57a76425(musicHistory);
//                }
//            });
//        }
//
//        public void m681x57a76425(MusicHistory musicHistory) {
//            MusicDatabaseControl.getInstance().removeMusicById(musicHistory.getId());
//            MusicFavoriteUtil.addFavoriteMusicId(MusicHistoryTabFragment.this.mContext, musicHistory.getId(), false);
//            Utility.deleteAMusic(MusicHistoryTabFragment.this.mContext, musicHistory.getMusics());
//        }
//    }
//
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.mContext = context;
//    }
//
//    public MusicHistoryPresenter createPresenter() {
//        Context context = this.mContext;
//        return new MusicHistoryPresenter(context, this, new MusicDataRepository(requireActivity()));
//    }
//
//    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
//        View inflate = layoutInflater.inflate(R.layout.fragment_music_info_tab, viewGroup, false);
//        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.rv_content_tab);
//        this.refreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh);
//        this.tvTotal = (TextView) inflate.findViewById(R.id.tv_total);
//        this.loading = (ProgressBar) inflate.findViewById(R.id.loading);
//        inflate.findViewById(R.id.iv_sort).setVisibility(4);
//        ImageView imageView = (ImageView) inflate.findViewById(R.id.iv_delete);
//        imageView.setVisibility(0);
//        imageView.setOnClickListener(new OnClickListener() {
//            public final void onClick(View view) {
//                onDeleteClick();
//            }
//        });
//        this.mAdapter = new MusicHistoryAdapter(requireActivity(), new ArrayList(), this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
//        recyclerView.setAdapter(this.mAdapter);
//        ItemAnimator itemAnimator = recyclerView.getItemAnimator();
//        if (itemAnimator instanceof SimpleItemAnimator) {
//            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
//        }
//        this.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            public final void onRefresh() {
//                if (mPresenter != null) {
//                    ((MusicHistoryPresenter) mPresenter).openMusicHistoryTab();
//                }
//            }
//        });
//        return inflate;
//    }
//
//
//    public void onViewCreated(View view, Bundle bundle) {
//        super.onViewCreated(view, bundle);
//       
//    }
//
//    public void onResume() {
//        super.onResume();
//        if (this.mPresenter != null) {
//            ((MusicHistoryPresenter) this.mPresenter).openMusicHistoryTab();
//        }
//        FirebaseAnalyticsUtils.putScreenChecking(this.mContext, "Music_History_Tab");
//    }
//
//    public void onDeleteClick() {
//        new QuestionDialogBuilder(this.mContext, new OkButtonClickListener() {
//            public void onCancelClick() {
//            }
//
//            public void onOkClick() {
//                if (MusicHistoryTabFragment.this.mPresenter != null) {
//                    ((MusicHistoryPresenter) MusicHistoryTabFragment.this.mPresenter).deleteAllMusicHistory();
//                }
//                if (MusicHistoryTabFragment.this.mAdapter != null) {
//                    MusicHistoryTabFragment.this.mAdapter.updateMusicHistory(new ArrayList());
//                }
//            }
//        }).setTitle(R.string.delete_all, this.mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_all_history_video).build().show();
//    }
//
//    public void updateMusicHistoryList(List<MusicHistory> list) {
//        this.loading.setVisibility(8);
//        this.refreshLayout.setVisibility(0);
//        this.refreshLayout.setRefreshing(false);
//        this.tvTotal.setText(getString(R.string.all_history, Integer.valueOf(list.size())));
//        MusicHistoryAdapter musicHistoryAdapter = this.mAdapter;
//        if (musicHistoryAdapter != null) {
//            musicHistoryAdapter.updateMusicHistory(list);
//        }
//    }
//
//    public void onMusicOptionSelect(final MusicHistory musicHistory, int i, final int i2) {
//        String str = FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_HISTORY_MORE;
//        if (i == 0) {
//            new QuestionDialogBuilder(this.mContext, new OkButtonClickListener() {
//                public void onCancelClick() {
//                }
//
//                public void onOkClick() {
//                    if (MusicHistoryTabFragment.this.mPresenter != null) {
//                        ((MusicHistoryPresenter) MusicHistoryTabFragment.this.mPresenter).deleteAHistoryMusicById(musicHistory.getId());
//                    }
//                    if (MusicHistoryTabFragment.this.mAdapter != null) {
//                        MusicHistoryTabFragment.this.mAdapter.removeItemPosition(i2);
//                    }
//                }
//            }).setTitle(R.string.delete, this.mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_a_history_video).build().show();
//            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_remove_from_history");
//        } else if (i == 1) {
//            new QuestionDialogBuilder(this.mContext, new AnonymousClass5(i2, musicHistory)).setTitle(R.string.delete, this.mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
//            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_delete_item");
//        } else if (i == 2) {
//            new MediaInfoDialogBuilder(this.mContext, musicHistory.getMusics()).build().show();
//            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_info");
//        }
//    }
//
//    public void deleteAllHistory() {
//        MusicHistoryAdapter musicHistoryAdapter = this.mAdapter;
//        if (musicHistoryAdapter != null) {
//            musicHistoryAdapter.removeAllItem();
//        }
//    }
//}
