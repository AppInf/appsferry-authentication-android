package com.appsferry.user.google.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class ThreadUtil {

    private ThreadUtil() {

    }

    public static void runOnUiThread(@NonNull Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            (new Handler(Looper.getMainLooper())).post(runnable);
        }

    }
}
