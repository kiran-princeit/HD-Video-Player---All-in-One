package com.hdvideoplayer.smartplayer.player.fragments.music;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Audio.Albums;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.activities.MusicAlbumActivity;
import com.hdvideoplayer.smartplayer.player.adapters.music.MusicAlbumAdapter;
import com.hdvideoplayer.smartplayer.player.adsprosimple.AdManager;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicAlbum;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.fragments.BaseFragment;
import com.hdvideoplayer.smartplayer.player.presenter.music.MusicAlbumPresenter;
import com.hdvideoplayer.smartplayer.player.util.FirebaseAnalyticsUtils;
import com.hdvideoplayer.smartplayer.player.view.music.MusicAlbumView;

import java.util.ArrayList;
import java.util.List;

public class MusicAlbumFragment extends BaseFragment<MusicAlbumPresenter> implements MusicAlbumView, MusicAlbumAdapter.AlbumClickListener {
    private final ContentObserver contentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean z, Uri uri) {
            if (MusicAlbumFragment.this.mPresenter != null) {
                ((MusicAlbumPresenter) MusicAlbumFragment.this.mPresenter).loadMusicAlbumList();
            }
        }
    };
    private ProgressBar loading;
    private MusicAlbumAdapter mAdapter;
    private Context mContext;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvTotal;
    ImageView iv_empty;

    public void onAlbumOptionSelect(MusicAlbum musicAlbum, int i, int i2) {
    }

    public void onUpdateMusicList(List<MusicInfo> list) {
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public MusicAlbumPresenter createPresenter() {
        return new MusicAlbumPresenter(this.mContext, this, new MusicDataRepository(requireActivity()));
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_music_main, viewGroup, false);

        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.rv_content_tab);
        this.refreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh);
        this.tvTotal = (TextView) inflate.findViewById(R.id.tv_total);
        this.loading = (ProgressBar) inflate.findViewById(R.id.loading);
        this.iv_empty = (ImageView) inflate.findViewById(R.id.iv_empty);
        inflate.findViewById(R.id.iv_sort).setVisibility(4);

        this.mAdapter = new MusicAlbumAdapter(this.mContext, new ArrayList(), this);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this.mContext);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(this.mAdapter);
        ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        this.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public final void onRefresh() {
                if (mPresenter != null) {
                    ((MusicAlbumPresenter) mPresenter).loadMusicAlbumList();
                }
            }
        });
        return inflate;
    }

    public void onResume() {
        super.onResume();
        if (this.mPresenter != null) {
            ((MusicAlbumPresenter) this.mPresenter).loadMusicAlbumList();
        }
        FirebaseAnalyticsUtils.putScreenChecking(this.mContext, "Music_Album_Tab");
        requireActivity().getContentResolver().registerContentObserver(Albums.EXTERNAL_CONTENT_URI, true, this.contentObserver);
    }

    public void onPause() {
        super.onPause();
        requireActivity().getContentResolver().unregisterContentObserver(this.contentObserver);
    }
    public void onAlbumClick(int i, MusicAlbum musicAlbum) {
        AdManager.showInterstitial(getActivity(), () -> {
            Intent intent = new Intent(getActivity(), MusicAlbumActivity.class);
            intent.putExtra("EXTRA_MUSIC_ALBUM", musicAlbum); // Pass the album data
            startActivity(intent);
        });
    }


    public void onUpdateAlbum(List<MusicAlbum> list) {
        this.loading.setVisibility(View.GONE);
        this.refreshLayout.setVisibility(View.VISIBLE);
        this.refreshLayout.setRefreshing(false);
        this.tvTotal.setText(getString(R.string.all_album, Integer.valueOf(list.size())));

        MusicAlbumAdapter musicAlbumAdapter = this.mAdapter;
        if (musicAlbumAdapter != null) {
            musicAlbumAdapter.updateAlbumList(list);
        }

        if (list == null || list.isEmpty()) {
            iv_empty.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }
}
