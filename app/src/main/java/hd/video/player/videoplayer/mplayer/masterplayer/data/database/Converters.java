package hd.video.player.videoplayer.mplayer.masterplayer.data.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class Converters {
    public static List<Long> fromString(String str) {
        return str == null ? null : (List) new Gson().fromJson(str, new TypeToken<ArrayList<Long>>() {
        }.getType());
    }

    public static String fromArrayList(List<Long> list) {
        return list == null ? null : new Gson().toJson((Object) list, new TypeToken<ArrayList<Long>>() {
        }.getType());
    }
}
