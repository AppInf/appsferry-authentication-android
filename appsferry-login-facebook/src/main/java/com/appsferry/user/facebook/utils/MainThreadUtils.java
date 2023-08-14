package com.appsferry.user.facebook.utils;


import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class MainThreadUtils {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(@NonNull Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }
}
