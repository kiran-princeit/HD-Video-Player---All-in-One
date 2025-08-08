package com.hdvideoplayer.smartplayer.player.adapters.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.util.BlurTransformation;
import com.hdvideoplayer.smartplayer.player.util.music.MusicPlayerUtils;

public class MusicArtPagerAdapter extends RecyclerView.Adapter<MusicArtPagerAdapter.ViewHolder> {

    private Context context;
    private List<MusicInfo> musicArtPaths;

    public MusicArtPagerAdapter(Context context, List<MusicInfo> musicArtPaths) {
        this.context = context;
        this.musicArtPaths = musicArtPaths;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music_art, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicInfo currentSong = musicArtPaths.get(position);
        Glide.with(context)
                .load(MusicPlayerUtils.getThumbnailOfSong(context, currentSong.getPath(), 280))
                .placeholder(R.drawable.ic_music_album)
                .into(holder.imgMusicArt); // Replace with your ImageView reference

        Glide.with(context).
                load(MusicPlayerUtils.getThumbnailOfSong(context, currentSong.getPath(), 280))
                .transform(new BlurTransformation(context)).into(holder.imgMusicArtBlur);
    }

    @Override
    public int getItemCount() {
        return musicArtPaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMusicArt;
        RoundedImageView imgMusicArtBlur;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMusicArt = itemView.findViewById(R.id.iv_music_art);
            imgMusicArtBlur = itemView.findViewById(R.id.iv_music_art_blur);
        }
    }
}

