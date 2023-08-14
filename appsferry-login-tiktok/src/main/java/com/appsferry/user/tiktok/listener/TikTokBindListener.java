package com.appsferry.user.tiktok.listener;

public interface TikTokBindListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
