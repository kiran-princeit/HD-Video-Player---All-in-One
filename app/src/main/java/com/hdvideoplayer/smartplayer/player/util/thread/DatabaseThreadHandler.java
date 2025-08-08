package com.hdvideoplayer.smartplayer.player.util.thread;

import android.os.Handler;
import android.os.HandlerThread;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class DatabaseThreadHandler {
    private static final String TAG = "DatabaseThreadHandler";
    private static final int TIME_OUT = 5000;
    private final Handler mDatabaseHandler;
    private final HandlerThread mDatabaseThread;

    private static class InstanceHolder {
        private static final DatabaseThreadHandler INSTANCE = new DatabaseThreadHandler();

        private InstanceHolder() {
        }
    }

    private DatabaseThreadHandler() {
        HandlerThread handlerThread = new HandlerThread("database-thread");
        this.mDatabaseThread = handlerThread;
        handlerThread.start();
        this.mDatabaseHandler = new Handler(handlerThread.getLooper());
    }

    public static DatabaseThreadHandler getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void run(Runnable runnable) {
        if (ThreadManager.isMainThread()) {
            this.mDatabaseHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    public <T> T run(Callable<T> callable) {
        return run(callable, null);
    }

    public <T> T run(Callable<T> callable, T t) {
        long currentTimeMillis = System.currentTimeMillis();
        try {
            T t2;
            if (ThreadManager.isMainThread()) {
                FutureTask futureTask = new FutureTask(callable);
                this.mDatabaseHandler.post(futureTask);
                t2 = (T) futureTask.get(5000, TimeUnit.MILLISECONDS);
            } else {
                t2 = callable.call();
            }
            t = t2;
            System.currentTimeMillis();
            return t;
        } catch (Exception e) {
            int i = ((System.currentTimeMillis() - currentTimeMillis) > 5000 ? 1 : ((System.currentTimeMillis() - currentTimeMillis) == 5000 ? 0 : -1));
            e.printStackTrace();
            return t;
        } catch (Throwable th) {
            System.currentTimeMillis();
            throw th;
        }
    }
}
