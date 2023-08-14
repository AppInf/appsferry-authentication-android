package com.appsferry.user.google.listener;

public interface GoogleRequestScopeListener {

    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
