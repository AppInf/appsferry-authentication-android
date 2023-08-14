package com.appsferry.user.tiktok.handlers;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.tiktok.open.sdk.auth.AuthApi;
import com.tiktok.open.sdk.auth.AuthRequest;
import com.tiktok.open.sdk.auth.utils.PKCEUtils;
import com.tiktok.open.sdk.core.appcheck.TikTokAppCheckUtil;

public class TiktokApiHandler {

    private String clientKey;

    private String redirectUri;

    public String getRedirectUri() {
        return redirectUri;
    }

    public void init(String clientKey, String redirectUri) {
        this.clientKey = clientKey;
        this.redirectUri = redirectUri;
    }

    public boolean isAppInstalled(@NonNull Activity activity) {
        return TikTokAppCheckUtil.INSTANCE.isTikTokAppInstalled(activity);

    }

    public boolean authorize(@NonNull Activity activity, @NonNull String scope, String state) {
        AuthApi authApi = new AuthApi(activity);
        AuthRequest authRequest = new AuthRequest(clientKey, scope, redirectUri, PKCEUtils.INSTANCE.generateCodeVerifier(), state, "");
        return authApi.authorize(authRequest, AuthApi.AuthMethod.TikTokApp);
    }

}
