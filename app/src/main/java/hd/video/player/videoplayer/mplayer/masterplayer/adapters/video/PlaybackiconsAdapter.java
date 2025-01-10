package hd.video.player.videoplayer.mplayer.masterplayer.adapters.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import hd.video.player.videoplayer.mplayer.masterplayer.IconModel;
import hd.video.player.videoplayer.mplayer.masterplayer.R;

import java.util.ArrayList;

public class PlaybackiconsAdapter extends RecyclerView.Adapter<PlaybackiconsAdapter.viewHolder> {
    private final Context context;
    private final ArrayList<IconModel> iconModelArrayList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void OnItemClick(int i);
    }

    public PlaybackiconsAdapter(ArrayList<IconModel> arrayList, Context context2) {
        this.iconModelArrayList = arrayList;
        this.context = context2;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new viewHolder(LayoutInflater.from(this.context).inflate(R.layout.icon_layout, viewGroup, false), this.mListener);
    }

    public void onBindViewHolder(viewHolder viewholder, int i) {
        viewholder.icon.setImageResource(this.iconModelArrayList.get(i).getImageview());
        viewholder.iconname.setText(this.iconModelArrayList.get(i).getIcontitle());
    }

    public int getItemCount() {
        return this.iconModelArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView iconname;

        public viewHolder(View view, final OnItemClickListener onItemClickListener) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.playback_icon);
            this.iconname = (TextView) view.findViewById(R.id.icon_title);
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    int adapterPosition;
                    if (onItemClickListener != null && (adapterPosition = viewHolder.this.getAdapterPosition()) != -1) {
                        onItemClickListener.OnItemClick(adapterPosition);
                    }
                }
            });
        }
    }
}
