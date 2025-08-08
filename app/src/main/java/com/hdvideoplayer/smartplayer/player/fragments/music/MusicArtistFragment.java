package com.hdvideoplayer.smartplayer.player.fragments.music;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Audio.Artists;
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

import com.hdvideoplayer.smartplayer.player.fragments.BaseFragment;
import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.activities.MusicPlaylistActivity;
import com.hdvideoplayer.smartplayer.player.adapters.music.ArtistListAdapter;
import com.hdvideoplayer.smartplayer.player.adapters.music.ArtistListAdapter.Callback;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicArtist;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.music.MusicArtistPresenter;
import com.hdvideoplayer.smartplayer.player.util.FirebaseAnalyticsUtils;
import com.hdvideoplayer.smartplayer.player.view.music.MusicArtistView;



public class MusicArtistFragment extends BaseFragment<MusicArtistPresenter> implements MusicArtistView, Callback {

    private final ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (mPresenter != null) {
                mPresenter.loadArtists();
            }
        }
    };

    private ProgressBar loadingIndicator;
    private ArtistListAdapter artistAdapter;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView totalArtistsTextView;
    ImageView iv_empty;

    @Override
    public void onUpdateMusicOfArtist(List<MusicInfo> musicInfoList) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public MusicArtistPresenter createPresenter() {
        return new MusicArtistPresenter(context, this, new MusicDataRepository(requireActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_main, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_content_tab);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        totalArtistsTextView = view.findViewById(R.id.tv_total);
        loadingIndicator = view.findViewById(R.id.loading);
        this.iv_empty = (ImageView) view.findViewById(R.id.iv_empty);
        view.findViewById(R.id.iv_sort).setVisibility(View.GONE);  // Hide sort button

        artistAdapter = new ArtistListAdapter(context, new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(artistAdapter);

        ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> loadArtistTabData());

        return view;
    }

    private void loadArtistTabData() {
        if (mPresenter != null) {
            mPresenter.loadArtists();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.loadArtists();
        }
        FirebaseAnalyticsUtils.putScreenChecking(context, "Music_Artist_Tab");
        requireActivity().getContentResolver().registerContentObserver(Artists.EXTERNAL_CONTENT_URI, true, contentObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().getContentResolver().unregisterContentObserver(contentObserver);
    }

    public void onOpenArtist(List<MusicArtist> list) {
        this.loadingIndicator.setVisibility(View.GONE);
        this.swipeRefreshLayout.setVisibility(View.VISIBLE);
        this.swipeRefreshLayout.setRefreshing(false);

        totalArtistsTextView.setText(getString(R.string.all_artist, list.size()));

        if (artistAdapter != null) {
            artistAdapter.updateArtistList(list);
        }
        if (list == null || list.isEmpty()) {
            iv_empty.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onArtistSelected(MusicArtist musicArtist) {
        Intent intent = new Intent(getContext(), MusicPlaylistActivity.class);
        intent.putExtra("music_artist", musicArtist);
        intent.putExtra("type", 3);
        startActivity(intent);
    }
}


