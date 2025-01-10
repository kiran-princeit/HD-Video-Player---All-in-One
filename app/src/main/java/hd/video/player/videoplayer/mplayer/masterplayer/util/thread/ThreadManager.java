package hd.video.player.videoplayer.mplayer.masterplayer.util.thread;

import android.os.Looper;
import android.util.Log;
import java.util.concurrent.ExecutorService;

public class ThreadManager {
    private ExecutorService mThreadExecutor;

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public void shutdownThreadExecutor() {
        try {
            ExecutorService executorService = this.mThreadExecutor;
            if (executorService != null) {
                if (!executorService.isShutdown()) {
                    this.mThreadExecutor.shutdownNow();
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public boolean canExecute() {
        boolean isShutdown = this.mThreadExecutor.isShutdown();
        boolean isTerminated = this.mThreadExecutor.isTerminated();
        if (!isShutdown && !isTerminated) {
            return true;
        }
        Log.d("aaa", "mThreadExecutor is shutdown or terminated. isShutdown :" + isShutdown + ",isTerminated :" + isTerminated);
        return false;
    }
}
