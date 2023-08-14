package com.appsferry.login.utils;

import android.text.TextUtils;

public class MD5UtilsCompat {
    private MD5UtilsCompat() {
    }

    public static String encode(String str) {
        return TextUtils.isEmpty(str) ? "" : encode(str.getBytes());
    }

    public static String encode(byte[] bytes) {
        try {
            return DigestUtils.md5Hex(bytes);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
