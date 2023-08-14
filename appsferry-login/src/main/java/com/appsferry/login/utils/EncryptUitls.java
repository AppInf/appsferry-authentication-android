package com.appsferry.login.utils;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class EncryptUitls {
    public static final String VERSION = "01";
    public static final byte[] SLAT = {109, 117, 118, 96, 116, 114, 100, 113};
    private static final byte[] PUBLIC_KEY = {76, 72, 70, 101, 76, 64, 47, 70, 66, 82, 112, 70, 82, 72, 97, 50, 67, 80, 68, 65, 64, 80, 84, 64, 64, 51, 70, 77, 64, 67, 66, 65, 104, 80, 74, 65, 102, 80, 67, 79, 70, 102, 76, 82, 47, 73, 67, 69, 75, 96, 81, 54, 71, 77, 54, 100, 42, 54, 101, 112, 104, 119, 100, 83, 76, 106, 99, 47, 89, 56, 73, 77, 47, 83, 81, 102, 73, 55, 104, 104, 42, 54, 54, 46, 84, 77, 107, 56, 113, 118, 89, 96, 104, 74, 69, 52, 75, 100, 65, 88, 80, 81, 114, 103, 115, 79, 84, 70, 96, 48, 109, 78, 87, 67, 81, 77, 86, 73, 113, 121, 79, 120, 97, 104, 51, 76, 66, 114, 97, 84, 65, 49, 75, 85, 120, 121, 113, 105, 85, 72, 109, 71, 97, 64, 51, 114, 105, 51, 55, 97, 42, 51, 117, 119, 102, 100, 56, 98, 83, 74, 119, 50, 105, 56, 86, 101, 101, 98, 82, 79, 69, 49, 66, 117, 49, 69, 103, 104, 115, 72, 110, 54, 74, 112, 100, 117, 104, 51, 83, 49, 64, 86, 77, 98, 99, 119, 52, 47, 77, 85, 85, 114, 48, 79, 74, 55, 53, 77, 55, 118, 72, 67, 64, 80, 64, 65};

    public static String encryptPhone(String areaCode, String phoneNum) {
        String phone = phoneNum;
        if (!TextUtils.isEmpty(phone)) {
            phone = phone.replace(" ", "");
            phone = LocationUtils.filterAreaCode(areaCode) + phone;

            String rsaPhone = phone;
            try {
                PublicKey publicKey = RSAUtils.getPublicKey(PUBLIC_KEY);
                byte[] encryptByte = RSAUtils.encrypt(phone.getBytes(Charset.forName("UTF-8")), publicKey);
                rsaPhone = Hex.encodeHexString(encryptByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rsaPhone;
        }
        return phone;
    }

    public static String encryptEmail(String email) {
        if (!TextUtils.isEmpty(email)) {
            String rsaEmail = email;
            try {
                PublicKey publicKey = RSAUtils.getPublicKey(PUBLIC_KEY);
                byte[] encryptByte = RSAUtils.encrypt(email.getBytes(Charset.forName("UTF-8")), publicKey);
                rsaEmail = Hex.encodeHexString(encryptByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rsaEmail;
        }
        return email;
    }

    public static String encryptUid(String uid) {
        if (!TextUtils.isEmpty(uid)) {
            String rsaUid = uid;
            try {
                PublicKey publicKey = RSAUtils.getPublicKey(PUBLIC_KEY);
                byte[] encryptByte = RSAUtils.encrypt(uid.getBytes(Charset.forName("UTF-8")), publicKey);
                rsaUid = Hex.encodeHexString(encryptByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rsaUid;
        }
        return uid;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String encryptPassword(String password) {
        String encryptPwd = MD5UtilsCompat.encode(password + getDecodeString(SLAT));
        String encryptResult = VERSION + encryptPwd;
        return encryptResult;
    }

    public static String encrypt(String text) {
        String rsaText = text;
        if (!TextUtils.isEmpty(rsaText)) {
            try {
                PublicKey publicKey = RSAUtils.getPublicKey(PUBLIC_KEY);
                byte[] encryptByte = RSAUtils.encrypt(text.getBytes(Charset.forName("UTF-8")), publicKey);
                rsaText = Hex.encodeHexString(encryptByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rsaText;
    }

    private static String getDecodeString(byte[] bytes) {
        byte[] tmp = new byte[bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];
            if (b < Byte.MAX_VALUE) {
                tmp[i] = (byte) (b + 1);
            } else {
                tmp[i] = Byte.MIN_VALUE;
            }
        }
        return new String(tmp, StandardCharsets.UTF_8);
    }

}
