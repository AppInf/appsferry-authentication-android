package com.appsferry.user.google.listener;

import androidx.annotation.MainThread;

public interface GoogleGetTokenListener {

    @MainThread
    void onSuccess(String token);

    @MainThread
    void onFailed(int errorCode, String errorMsg);
}
