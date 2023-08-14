package com.appsferry.login.api;

import com.appsferry.core.parameters.Parameters;

public class Constants {
    public final static String APP_ID = "ik_appid";

    public final static String CV = "cv";

    public final static String UID = "uid";

    public final static String SID = "sid";

    public final static String LOGIN_VCD = "vcd01";

    public final static int VISITOR_TYPE = 2;

    public final static String ACCOUNT_TYPE_PHONE = "phone";

    public final static String ACCOUNT_TYPE_EMAIL = "email";

    public static String getAppid() {
        return Parameters.getInstance().get(APP_ID);
    }

    public static String getCv() {
        return Parameters.getInstance().get(CV);
    }

    public static String getUid() {
        return Parameters.getInstance().get(UID);
    }

    public static String getSid() {
        return Parameters.getInstance().get(SID);
    }

    public final static String LOGIN_APP_TYPE = "android";

    public final static String LOGIN_TYPE_GOOGLE = "google";

    public final static String LOGIN_TYPE_FACEBOOK = "facebook";

    public final static String LOGIN_TYPE_TIKTOK = "tiktok";

    public final static String LOGIN_TYPE_SNAPCHAT = "snapchat";
}
