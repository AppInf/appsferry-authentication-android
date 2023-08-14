package com.appsferry.user.google.listener;

public interface GoogleDisconnectListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
