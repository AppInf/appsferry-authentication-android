package com.appsferry.user.tiktok.listener;

public interface TikTokAuthListener {
    void onSuccess(String authCode);

    void onFailed(int errorCode, String errorMsg);
}
