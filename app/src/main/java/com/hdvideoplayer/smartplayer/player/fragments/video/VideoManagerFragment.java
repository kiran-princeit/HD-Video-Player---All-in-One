package com.hdvideoplayer.smartplayer.player.fragments.video;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adapters.video.VideoTabPagerAdapter;

public class VideoManagerFragment extends Fragment {
    private Context mContext;
    ViewPager2 viewPager;
    TabLayout tab;
    ImageView iv_icon;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_video_manager, viewGroup, false);

        iv_icon = inflate.findViewById(R.id.iv_icons);
        viewPager = (ViewPager2) inflate.findViewById(R.id.view_pager);

        viewPager.setAdapter(new VideoTabPagerAdapter(getActivity()));
        viewPager.setUserInputEnabled(false);
        viewPager.setCurrentItem(0);
        tab = inflate.findViewById(R.id.tab);

        String[] tabTitles = {getActivity().getString(R.string.video), getActivity().getString(R.string.folder), getActivity().getString(R.string.playlist), getActivity().getString(R.string.history)};

        new TabLayoutMediator(tab, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

        Glide.with(this)
                .asGif()
                .load(R.drawable.icon)
                .into(iv_icon);

        inflate.findViewById(R.id.iv_search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new SearchVideoDialogFragment().show(getChildFragmentManager().beginTransaction(), "dialog_search");
            }
        });
        return inflate;
    }

}
