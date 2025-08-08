package com.hdvideoplayer.smartplayer.player.fragments.video;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;

import com.hdvideoplayer.smartplayer.player.adapters.video.VideoHistoryAdapter;
import com.hdvideoplayer.smartplayer.player.data.datasource.VideoDatabaseControl;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoHistory;
import com.hdvideoplayer.smartplayer.player.data.utils.VideoFavoriteUtil;
import com.hdvideoplayer.smartplayer.player.dialog.MediaInfoDialogBuilder;
import com.hdvideoplayer.smartplayer.player.dialog.QuestionDialogBuilder;
import com.hdvideoplayer.smartplayer.player.util.SharedPreferencesUtils;
import com.hdvideoplayer.smartplayer.player.util.Utility;
import com.hdvideoplayer.smartplayer.player.util.thread.ThreadExecutor;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private VideoHistoryAdapter adapter;
    private List<VideoHistory> mHistories;
    Context context;
    private ProgressBar loading;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvTotal;
    ImageView iv_empty;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.rv_content_tab);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        loading = view.findViewById(R.id.loading);
        tvTotal = view.findViewById(R.id.tv_total_video);
        iv_empty = view.findViewById(R.id.iv_empty);
        view.findViewById(R.id.iv_delete).setOnClickListener(v -> onDeleteClick());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new VideoHistoryAdapter(getActivity(), mHistories, new VideoHistoryAdapter.Callback() {
            @Override
            public void onHistoryOptionSelect(VideoHistory videoHistory, int position, int action) {
                HistoryFragment.this.onHistoryOptionSelect(videoHistory, position, action);
            }
        });
        recyclerView.setAdapter(adapter);
        loadHistoryData();
        setupRefreshLayout();

        return view;
    }

    private void loadHistoryData() {
        List<VideoHistory> videoHistoryList = SharedPreferencesUtils.getHistoryList(getContext(), "video_history_key");
        updateHistoryVideos(videoHistoryList);
    }


    private void setupRefreshLayout() {
        mHistories = SharedPreferencesUtils.getHistoryList(getContext(), "video_history_key");
        refreshLayout.setOnRefreshListener(() -> {
            loadHistoryData();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistoryData();
    }

    public void onDeleteClick() {
        new QuestionDialogBuilder(context, new QuestionDialogBuilder.OkButtonClickListener() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onOkClick() {
                deleteAllHistory();

                tvTotal.setText(getString(R.string.all_history, 0));  // Update total count to 0
            }
        })
                .setTitle(R.string.delete_all, context.getResources().getColor(R.color.color_FF6666))
                .setQuestion(R.string.question_remove_all_history_video)
                .build()
                .show();
    }

    private void deleteAllHistory() {
        SharedPreferencesUtils.putHistoryList(getActivity(), "video_history_key", new ArrayList<>());
        mHistories.clear();
        adapter.notifyDataSetChanged();
        tvTotal.setText(getString(R.string.all_history, 0));
        Toast.makeText(getContext(), "History deleted", Toast.LENGTH_SHORT).show();
    }

    public void updateHistoryVideos(List<VideoHistory> videos) {
        loading.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        tvTotal.setText(getString(R.string.all_history, videos.size()));
        adapter.updateHistory(videos);

        if (videos == null || videos.isEmpty()) {
            iv_empty.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onHistoryOptionSelect(VideoHistory videoHistory, int option, int position) {
        switch (option) {
            case 0:
                showDeleteConfirmationDialog(videoHistory, position, false);
                break;
            case 1:
                showDeleteConfirmationDialog(videoHistory, position, true);
                break;
            case 2:
                new MediaInfoDialogBuilder(context, videoHistory.getVideo()).build().show();
                break;
        }
    }

    private void showDeleteConfirmationDialog(VideoHistory videoHistory, int position, boolean deleteFile) {
        new QuestionDialogBuilder(context, new QuestionDialogBuilder.OkButtonClickListener() {
            @Override
            public void onCancelClick() {
            }

            @Override
            public void onOkClick() {
                if (adapter != null) {
                    adapter.removeItemPosition(position);
                }
                if (deleteFile) {
                    handleFileDeletion(videoHistory);
                }
                removeFromHistory(videoHistory);
            }
        })
                .setTitle(R.string.delete, context.getResources().getColor(R.color.color_FF6666))
                .setQuestion(deleteFile ? R.string.question_remove_file : R.string.question_remove_a_history_video)
                .build()
                .show();
    }

    private void removeFromHistory(VideoHistory videoHistory) {
        List<VideoHistory> videoHistoryList = SharedPreferencesUtils.getHistoryList(context, "video_history_key");

        Iterator<VideoHistory> iterator = videoHistoryList.iterator();
        while (iterator.hasNext()) {
            VideoHistory history = iterator.next();
            // Compare the IDs using '==' for primitive 'long'
            if (history.getId() == videoHistory.getId()) {
                iterator.remove();
                break;
            }
        }

        SharedPreferencesUtils.putHistoryList(context, "video_history_key", videoHistoryList);

        if (adapter != null) {
            adapter.updateHistory(videoHistoryList); // Update the RecyclerView with the updated list
        }
    }

    private void handleFileDeletion(VideoHistory videoHistory) {
        ThreadExecutor.runOnDatabaseThread(() -> {
            VideoDatabaseControl.getInstance().removeVideoById(videoHistory.getId());
            VideoFavoriteUtil.addFavoriteVideoId(context, videoHistory.getId(), false);
            Utility.deleteAVideo(context, videoHistory.getVideo());
        });
    }
}
