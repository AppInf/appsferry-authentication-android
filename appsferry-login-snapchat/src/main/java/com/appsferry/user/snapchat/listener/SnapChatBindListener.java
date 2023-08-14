package com.appsferry.user.snapchat.listener;

public interface SnapChatBindListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
