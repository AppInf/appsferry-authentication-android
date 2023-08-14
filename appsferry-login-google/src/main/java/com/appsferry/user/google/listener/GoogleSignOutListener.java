package com.appsferry.user.google.listener;

public interface GoogleSignOutListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
