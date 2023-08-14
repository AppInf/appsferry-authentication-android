package com.appsferry.user.google.listener;

public interface GoogleUnBindListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
