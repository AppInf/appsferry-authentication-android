package com.appsferry.user.facebook.listener;

public interface FacebookBindListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
