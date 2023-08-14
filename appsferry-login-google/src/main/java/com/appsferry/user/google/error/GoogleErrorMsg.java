package com.appsferry.user.google.error;

import com.appsferry.login.api.UserSDK;
import com.appsferry.user.google.R;

public class GoogleErrorMsg {
    private GoogleErrorMsg() {
    }

    public static String signInFailed() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_signin_failed);
    }

    public static String signInCancel() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_signin_cancel);
    }

    public static String networkError() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_network_error);
    }

    public static String signInError() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_singin_error);
    }

    public static String signOutFailed() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_signout_failed);
    }

    public static String disconnectFailed() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_disconnection_failed);
    }

    public static String requestScopeError() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_request_scope_error);
    }

    public static String getTokenError() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_get_token_error);
    }

    public static String clearTokenError() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_clear_token_error);
    }

    public static String getGenerics() {
        return UserSDK.getInstance().getContext().getString(R.string.user_go_generics_error);
    }
}
