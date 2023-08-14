package com.appsferry.user.snapchat.listener;

public interface SnapChatUnbindListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
