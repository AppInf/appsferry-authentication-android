package com.appsferry.user.facebook.error;

import com.appsferry.login.api.UserSDK;
import com.appsferry.user.facebook.R;

public class FacebookErrorMsg {
    private FacebookErrorMsg() {
    }

    /**
     * facebook auth failed
     */
    public static String authFailedMsg() {
        return UserSDK.getInstance().getContext().getString(R.string.user_fb_auth_failed);
    }

    /**
     * facebook auth cancel
     */
    public static String authCancelMsg() {
        return UserSDK.getInstance().getContext().getString(R.string.user_fb_auth_cancel);
    }

    public static String getGenerics() {
        return UserSDK.getInstance().getContext().getString(R.string.user_fb_generics_error);
    }
}
