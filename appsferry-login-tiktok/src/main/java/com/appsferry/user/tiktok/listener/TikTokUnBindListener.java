package com.appsferry.user.tiktok.listener;

public interface TikTokUnBindListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
