package com.appsferry.user.snapchat.listener;

import com.appsferry.login.entity.user.UserModel;
import com.appsferry.login.entity.user.UserProfile;

public interface SnapChatLoginListener<T extends UserModel<? extends UserProfile>> {

    void onSuccess(T t);

    void onFailed(int errorCode, String errorMsg);
}
