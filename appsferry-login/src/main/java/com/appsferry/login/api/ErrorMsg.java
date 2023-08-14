package com.appsferry.login.api;

import com.appsferry.user.R;

public class ErrorMsg {

    public static String AREA_CODE_INVALID() {
        return UserSDK.getInstance().getContext().getString(R.string.user_invalid_area_code);
    }

    public static String PHONE_NUM_INVALID() {
        return UserSDK.getInstance().getContext().getString(R.string.user_invalid_phone_number);
    }

    public static String PHONE_CODE_INVALID() {
        return UserSDK.getInstance().getContext().getString(R.string.user_invalid_phone_code);
    }

    public static String NETWORK_ERROR() {
        return UserSDK.getInstance().getContext().getString(R.string.user_network_error);
    }

    public static String SERVER_DATA_ERROR() {
        return UserSDK.getInstance().getContext().getString(R.string.user_server_data_error);
    }

    public static String SDK_ERROR() {
        return UserSDK.getInstance().getContext().getString(R.string.user_sdk_error);
    }

    public static final String EMAIL_INVALID() {
        return UserSDK.getInstance().getContext().getString(R.string.user_invalid_email_address);
    }

    public static final String EMAIL_CODE_INVALID() {
        return UserSDK.getInstance().getContext().getString(R.string.user_invalid_email_code);
    }

    public static final String ACCOUNT_PASSWORD_INVALID() {
        return UserSDK.getInstance().getContext().getString(R.string.user_empty_password);
    }

}
