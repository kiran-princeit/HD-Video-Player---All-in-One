package hd.video.player.videoplayer.mplayer.masterplayer.data.repository;

import java.util.List;

public interface ILoaderRepository<T> {

    public interface InsertDataListener {
        void onError();

        void onSuccess();
    }

    public interface LoadDataListener<T> {
        void onError();

        void onSuccess(List<T> list);
    }
}
