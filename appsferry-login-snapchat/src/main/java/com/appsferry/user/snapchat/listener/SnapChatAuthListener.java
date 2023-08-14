package com.appsferry.user.snapchat.listener;

import com.snap.loginkit.models.MeData;

public interface SnapChatAuthListener {

    void onSuccess(MeData meData);

    void onFailed(int errorCode, String errorMsg);
}
