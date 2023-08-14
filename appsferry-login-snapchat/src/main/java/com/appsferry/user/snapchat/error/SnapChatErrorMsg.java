package com.appsferry.user.snapchat.error;

import com.appsferry.login.api.UserSDK;
import com.appsferry.user.snapchat.R;

public class SnapChatErrorMsg {
    private SnapChatErrorMsg() {
    }

    public static String authFailedMsg() {
        return UserSDK.getInstance().getContext().getString(R.string.user_sc_auth_failed);
    }

    public static String getUserDataFailedMsg() {
        return UserSDK.getInstance().getContext().getString(R.string.user_sc_get_data_failed);
    }

    public static String getGenerics() {
        return UserSDK.getInstance().getContext().getString(R.string.user_sc_generics_error);
    }
}
