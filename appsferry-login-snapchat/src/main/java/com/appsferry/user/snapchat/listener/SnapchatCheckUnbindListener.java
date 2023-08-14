package com.appsferry.user.snapchat.listener;

import com.appsferry.user.snapchat.model.SnapChatCheckUnbindModel;

public interface SnapchatCheckUnbindListener {

    void onSuccess(SnapChatCheckUnbindModel chatCheckUnbindModel);

    void onFailed(int errorCode, String errorMsg);
}
