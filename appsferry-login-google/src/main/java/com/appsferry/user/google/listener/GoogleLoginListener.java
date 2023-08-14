package com.appsferry.user.google.listener;

import com.appsferry.login.entity.user.UserModel;

public interface GoogleLoginListener<T extends UserModel> {

    void onSuccess(T t);

    void onFailed(int errorCode, String errorMsg);
}
