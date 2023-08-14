package com.appsferry.user.facebook.listener;

public interface FacebookUnBindListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
