package com.appsferry.user.google.listener;

public interface GoogleBindListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
