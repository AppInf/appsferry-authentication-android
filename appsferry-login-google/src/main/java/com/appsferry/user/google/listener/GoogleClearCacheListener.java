package com.appsferry.user.google.listener;

import androidx.annotation.MainThread;

public interface GoogleClearCacheListener {

    @MainThread
    void onSuccess();

    @MainThread
    void onFailed(int errorCode, String errorMsg);

}
