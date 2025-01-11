package hd.video.player.videoplayer.mplayer.masterplayer.adapters.music;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicAlbum;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;

public class MusicAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
    private AlbumClickListener albumClickListener;
    private Context context;
    private List<MusicAlbum> musicAlbums = new ArrayList<>();

    public interface AlbumClickListener {
        void onAlbumClick(int position, MusicAlbum musicAlbum);

        void onAlbumOptionSelect(MusicAlbum musicAlbum, int optionId, int optionType);
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View view) {
            super(view);
        }
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView albumArtImageView;
        TextView albumNameTextView;
        TextView artistNameTextView;

        public AlbumViewHolder(View view) {
            super(view);
            this.albumArtImageView = view.findViewById(R.id.iv_album_art);
            this.albumNameTextView = view.findViewById(R.id.tv_album_name);
            this.artistNameTextView = view.findViewById(R.id.tv_artist);
        }
    }

    public MusicAlbumAdapter(Context context, List<MusicAlbum> musicAlbums, AlbumClickListener albumClickListener) {
        this.context = context;
        this.albumClickListener = albumClickListener;
        this.musicAlbums = musicAlbums;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType != -1) {
            return new AlbumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_album, parent, false));
//        }
//        View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_data, parent, false);
//        return new EmptyViewHolder(emptyView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//        if (holder.getItemViewType() != -1) {
            AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
            final MusicAlbum musicAlbum = musicAlbums.get(position);
            albumViewHolder.albumNameTextView.setText(musicAlbum.getAlbumName());
            albumViewHolder.artistNameTextView.setText(musicAlbum.getArtistName());
            Glide.with(context)
                    .load(ContentUris.withAppendedId(artworkUri, musicAlbum.getAlbumId()))
                    .placeholder(R.drawable.ic_music_album)
                    .centerCrop()
                    .error(R.drawable.ic_music_album)
                    .into(albumViewHolder.albumArtImageView);
            albumViewHolder.itemView.setOnClickListener(view -> onAlbumClick(position, musicAlbum, view));
//        }
    }

    private void onAlbumClick(int position, MusicAlbum musicAlbum, View view) {
        albumClickListener.onAlbumClick(position, musicAlbum);
    }

    public void updateAlbumList(List<MusicAlbum> albums) {
        this.musicAlbums = albums;
        Collections.sort(albums, (album1, album2) -> album1.getAlbumName().compareToIgnoreCase(album2.getAlbumName()));
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (musicAlbums == null || musicAlbums.isEmpty()) ? -1 : 0;
    }

    @Override
    public int getItemCount() {
        return (musicAlbums == null || musicAlbums.isEmpty()) ? 1 : musicAlbums.size();
    }
}
