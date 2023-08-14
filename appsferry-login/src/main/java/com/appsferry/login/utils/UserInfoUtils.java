package com.appsferry.login.utils;

import com.appsferry.core.parameters.Parameters;
import com.appsferry.core.storage.FileStorage;
import com.appsferry.core.storage.SpStorage;
import com.appsferry.login.api.Constants;

import java.util.HashMap;

public class UserInfoUtils {

    public static String getCurrentUidFromFile() {
        return SpStorage.Companion.getDEFAULT().getString("cur_user", "");
    }

    public static void setCurrentUidFromFile(String uid) {
        SpStorage.Companion.getDEFAULT().saveString("cur_user", uid);
    }

    public static String getUserInfoFromFile(String uid) {
        String userInfoKey = "user-" + uid;
        return FileStorage.Companion.getENCRYPT_DEFAULT().getString(MD5UtilsCompat.encode(userInfoKey), "");
    }

    public static void setUserInfoToFile(String uid, String userinfo) {
        String userInfoKey = "user-" + uid;
        FileStorage.Companion.getENCRYPT_DEFAULT().saveString(MD5UtilsCompat.encode(userInfoKey), userinfo);
    }

    public static String getAppid() {
        return Parameters.getInstance().get(Constants.APP_ID);
    }

    public static String getCv() {
        return Parameters.getInstance().get(Constants.CV);
    }

    public static void setUidSid(String uid, String sid) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("sid", sid);
        Parameters.getInstance().put(hashMap);
    }
}
