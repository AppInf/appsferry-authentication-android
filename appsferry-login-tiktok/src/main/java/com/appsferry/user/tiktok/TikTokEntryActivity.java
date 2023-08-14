package com.appsferry.user.tiktok;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.appsferry.user.tiktok.handlers.TikTokResponseHandler;
import com.appsferry.user.tiktok.handlers.TiktokAuthHandler;
import com.tiktok.open.sdk.auth.AuthApi;
import com.tiktok.open.sdk.auth.AuthResponse;

public class TikTokEntryActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TikTokManager.TAG, "onCreate");
        AuthApi authApi = new AuthApi(this);
        AuthResponse response = authApi.getAuthResponseFromIntent(getIntent(),
                TiktokAuthHandler.getInstance().getTiktokApiHandler().getRedirectUri());
        TikTokResponseHandler.dispatchResponse(response);
        finish();
    }
}
