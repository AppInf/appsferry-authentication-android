package com.appsferry.user.tiktok.handlers;

import android.util.Log;

import com.appsferry.core.gson.AfGson;
import com.appsferry.user.tiktok.TikTokManager;
import com.tiktok.open.sdk.auth.AuthResponse;

public class TikTokResponseHandler {
    public static void dispatchResponse(AuthResponse response) {
        if (response instanceof AuthResponse) {
            Log.i(TikTokManager.TAG, " response:" + AfGson.Companion.get().toJson(response));
            TiktokAuthHandler.getInstance().dispatchResponse(response);
        }
    }
}
