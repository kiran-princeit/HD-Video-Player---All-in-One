package hd.video.player.videoplayer.mplayer.masterplayer.fragments.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import hd.video.player.videoplayer.mplayer.masterplayer.R;

public class MusicManagerFragment extends Fragment {
    private TabLayout tabLayout;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_music_manager, viewGroup, false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Set up tabs
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.music)));
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.playlist)));
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.album)));
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.artist)));
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.history)));

        // Set default fragment
        loadFragment(new MusicMainFragment());

        // Tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new MusicMainFragment();
                        break;
                    case 1:
                        selectedFragment = new MusicPlaylistFragment();
                        break;
                    case 2:
                        selectedFragment = new MusicAlbumFragment();
                        break;
                    case 3:
                        selectedFragment = new MusicArtistFragment();
                        break;
                    case 4:
                        selectedFragment = new MusicHistoryFragment();
                        break;
                }
                loadFragment(selectedFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null && getChildFragmentManager() != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }
    }
}
