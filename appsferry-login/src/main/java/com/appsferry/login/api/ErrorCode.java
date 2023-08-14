package com.appsferry.login.api;

public class ErrorCode {
    /**
     * SDK internal error
     */
    public static final int SDK_ERROR = -10000;
    /**
     * network exception
     */
    public static final int NETWORK_ERROR = -9999;
    /**
     * server data invalid
     */
    public static final int SERVER_DATA_ERROR = -10001;
    /**
     * phone number invalid
     */
    public static final int PHONE_NUM_INVALID = -10030;
    /**
     * area code invalid
     */
    public static final int AREA_CODE_INVALID = -10031;
    /**
     * verify code invalid
     */
    public static final int PHONE_CODE_INVALID = -10032;
    /**
     * not current user
     */
    public static final int NOT_CURRENT_USER = -10033;
    /**
     * user not login
     */
    public static final int UN_LOGIN = -10034;

    /**
     * uid invalid
     */
    public static final int UID_INVALID = -10035;
    /**
     * token invalid
     */
    public static final int TOKEN_INVALID = -10036;
    /**
     * email invalid
     */
    public static final int EMAIL_INVALID = -10040;
    /**
     * email code invalid
     */
    public static final int EMAIL_CODE_INVALID = -10041;
    /**
     * account type invalid
     */
    public static final int ACCOUNT_TYPE_INVALID = -10043;
    /**
     * password invalid
     */
    public static final int ACCOUNT_PASSWORD_INVALID = -10042;
    /**
     * code verify error
     */
    public static final int CODE_VERIFY = 645;
}
