package com.appsferry.user.google.listener;

import com.appsferry.user.google.fragment.model.GoogleCheckUnbindModel;

public interface GoogleCheckUnbindListener {

    void onSuccess(GoogleCheckUnbindModel checkUnbindEntity);

    void onFailed(int errorCode, String errorMsg);
}
