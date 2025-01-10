package hd.video.player.videoplayer.mplayer.masterplayer.fragments.music;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Audio.Artists;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.ArtistListAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.ArtistListAdapter.Callback;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicArtist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.music.MusicArtistPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicArtistView;



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

    @Override
    public void onUpdateMusicOfArtist(List<MusicInfo> musicInfoList) {
        // You can implement the method here to update the artist's music list
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

        // Register content observer for external content changes
        requireActivity().getContentResolver().registerContentObserver(Artists.EXTERNAL_CONTENT_URI, true, contentObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister content observer to prevent leaks
        requireActivity().getContentResolver().unregisterContentObserver(contentObserver);
    }

    @Override
    public void onOpenArtist(List<MusicArtist> artistList) {
        loadingIndicator.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        totalArtistsTextView.setText(getString(R.string.all_artist, artistList.size()));

        if (artistAdapter != null) {
            artistAdapter.updateArtistList(artistList);
        }
    }

    @Override
    public void onArtistSelected(MusicArtist musicArtist) {
        // Show music playlist dialog for the selected artist
        new MusicPlaylistDialogFragment(3, musicArtist, new MusicPlaylistDialogFragment.Callback() {
            @Override
            public void onDialogDismiss() {
                reloadArtistTab();
            }
        }).show(getChildFragmentManager(), "dialog_artist_music");
    }

    private void reloadArtistTab() {
        if (mPresenter != null) {
            mPresenter.loadArtists();
        }
    }
}


