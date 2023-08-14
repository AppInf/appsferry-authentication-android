package com.appsferry.user.facebook.listener;

import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;

public interface FacebookLoginListener<T extends UserModel<? extends UserProfile>> {

    void onSuccess(T t);

    void onFailed(int errorCode, String errorMsg);
}
