package com.appsferry.user.tiktok.util;

import android.util.Log;

import com.appsferry.user.tiktok.TikTokManager;
import com.appsferry.user.tiktok.handlers.TiktokApiHandler;

public class TikTokAssert {
    private TikTokAssert() {
    }

    public static void assertion(TiktokApiHandler tiktokAPiHandler) {
        if (tiktokAPiHandler == null) {
            Log.e(TikTokManager.TAG, "Please initialize first !");
        }
    }

}
