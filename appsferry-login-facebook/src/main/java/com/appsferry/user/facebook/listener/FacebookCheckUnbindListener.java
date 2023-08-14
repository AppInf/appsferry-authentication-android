package com.appsferry.user.facebook.listener;

import com.appsferry.user.facebook.activity.model.FacebookCheckUnbindModel;

public interface FacebookCheckUnbindListener {

    void onSuccess(FacebookCheckUnbindModel checkUnbindEntity);

    void onFailed(int errorCode, String errorMsg);
}
