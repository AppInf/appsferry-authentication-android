package com.appsferry.user.google.util;

import android.util.Log;

import com.appsferry.user.google.GoogleManager;
import com.appsferry.user.google.handlers.GoogleApiHandler;

public class GoogleAssert {

    private GoogleAssert() {
    }

    public static void googleAssert(GoogleApiHandler googleApiHandler) {
        if (googleApiHandler == null) {
            Log.e(GoogleManager.TAG, "Please initialize first!");
        }
    }
}
