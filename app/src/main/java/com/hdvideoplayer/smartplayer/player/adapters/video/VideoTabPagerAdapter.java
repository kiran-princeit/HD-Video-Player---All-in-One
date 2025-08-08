package com.hdvideoplayer.smartplayer.player.adapters.video;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hdvideoplayer.smartplayer.player.fragments.video.HistoryFragment;
import com.hdvideoplayer.smartplayer.player.fragments.video.VideoFolderFragment;
import com.hdvideoplayer.smartplayer.player.fragments.video.VideoAllFragment;
import com.hdvideoplayer.smartplayer.player.fragments.video.VideoPlaylistFragment;

public class VideoTabPagerAdapter extends FragmentStateAdapter {
    public int getItemCount() {
        return 4;
    }

    public VideoTabPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public Fragment createFragment(int i) {
        if (i == 0) {
            return new VideoAllFragment();
        }
        if (i == 1) {
            return new VideoFolderFragment();
        }
        if (i == 2) {
            return new VideoPlaylistFragment();
        }
        if (i == 3) {
            return new HistoryFragment();
        }
        return new VideoAllFragment();
    }
}
