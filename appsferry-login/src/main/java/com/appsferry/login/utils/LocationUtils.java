package com.appsferry.login.utils;

import android.text.TextUtils;

public class LocationUtils {
    public static String filterAreaCode(String areaCode) {
        String area = areaCode;
        if (!TextUtils.isEmpty(area)) {
            if (area.contains("+")) {
                area = area.replace("+", "");
            }
            if (area.contains("＋")) {
                area = area.replace("＋", "");
            }
        }
        return area;
    }
}
