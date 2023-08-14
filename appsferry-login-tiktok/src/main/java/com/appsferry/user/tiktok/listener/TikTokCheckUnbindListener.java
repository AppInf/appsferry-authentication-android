package com.appsferry.user.tiktok.listener;

import com.appsferry.user.tiktok.model.TikTokCheckUnbindModel;

public interface TikTokCheckUnbindListener {

    void osSuccess(TikTokCheckUnbindModel tikTokCheckUnbindModel);

    void OnFailed(int errorCode, String errorMsg);
}
