package com.appsferry.user.tiktok.error;

import com.appsferry.login.api.UserSDK;
import com.appsferry.user.tiktok.R;

public class TikTokErrorMsg {
    private TikTokErrorMsg() {
    }

    public static String authFailedMsg() {
        return UserSDK.getInstance().getContext().getString(R.string.user_tt_auth_failed);
    }

    public static String getGenerics() {
        return UserSDK.getInstance().getContext().getString(R.string.user_tt_generics_error);
    }

}
