package com.appsferry.user.facebook.listener;

public interface FacebookAuthListener {

    void onSuccess(String token);

    void onFailed(int errorCode, String errorMsg, String fbMsg);
}
