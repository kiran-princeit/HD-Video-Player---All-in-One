package hd.video.player.videoplayer.mplayer.masterplayer.util.thread;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Callable;

public class ThreadExecutor {
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    public static void runOnMainThread(Runnable runnable) {
        runOnMainThread(runnable, false);
    }

    public static void runOnMainThread(Runnable runnable, boolean z) {
        if (!z) {
            sMainHandler.post(runnable);
        } else if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            sMainHandler.post(runnable);
        }
    }

    public static void runOnMainThread(Runnable runnable, long j) {
        sMainHandler.postDelayed(runnable, j);
    }

    public static void runOnDatabaseThread(Runnable runnable) {
        DatabaseThreadHandler.getInstance().run(runnable);
    }

    public static <T> T runOnDatabaseThread(Callable<T> callable) {
        return (T) DatabaseThreadHandler.getInstance().run((Callable) callable);
    }

    public static <T> T runOnDatabaseThread(Callable<T> callable, T t) {
        return DatabaseThreadHandler.getInstance().run(callable, t);
    }
}
